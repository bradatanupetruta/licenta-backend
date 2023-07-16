package com.licenta.backend.service;

import com.licenta.backend.dto.OrderDTO;
import com.licenta.backend.dto.OrderProductDTO;
import com.licenta.backend.dto.OrderStatusDTO;
import com.licenta.backend.model.*;
import com.licenta.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private UserService userService;
    private TabService tabService;
    private ProductService productService;
    private OrderProductService orderProductService;
    private IngredientService ingredientService;

    public OrderService(OrderRepository orderRepository, UserService userService,
                        TabService tabService, ProductService productService,
                        OrderProductService orderProductService, IngredientService ingredientService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.tabService = tabService;
        this.productService = productService;
        this.orderProductService = orderProductService;
        this.ingredientService = ingredientService;
    }

    public Order saveOrder(OrderDTO orderDTO) {
        Order order = getOrderToSave(orderDTO.getOrderId());
        User user = userService.getUserById(orderDTO.getUserId());
        Tab tab = tabService.getTabByIndex(orderDTO.getTabIndex());
        List<OrderProduct> savedOrderProducts = saveOrderProducts(order, orderDTO.getProducts());
        order.setServer(user);
        order.setTable(tab);
        order.setDate(changeDay(new Date()));
        order.setStatus(orderDTO.getStatus());
        order.setProducts(savedOrderProducts);
        return orderRepository.save(order);
    }

    public Order closeOrder(OrderDTO orderDTO) {
        Order order = getOrderToSave(orderDTO.getOrderId());
        order.setStatus(orderDTO.getStatus());
        return orderRepository.save(order);
    }

    public Order getActiveOrderByTab(int tab_index) {
        List<Order> ordes = orderRepository.findOrderByTableIndex(tab_index, "ACTIVE");
        if (ordes.isEmpty()) {
            return null;
        }
        return ordes.get(0);
    }

    public List<Order> getAll() {
        List<Order> orders = orderRepository.findAll();
        orders.sort(Comparator.comparing(Order::getDate));
        return orders;
    }

    public List<OrderStatusDTO> getOrderStatus() {
        List<OrderStatusDTO> result = new ArrayList<>();
        List<Order> orders = getAll();
        orders.sort(Comparator.comparing((Order::getDate)).reversed());
        for (Order order : orders) {
            OrderStatusDTO orderStatus = new OrderStatusDTO();
            orderStatus.setOrderId(order.getOrderid());
            orderStatus.setOrderStatus(order.getStatus());
            orderStatus.setServerId(order.getServer().getId());
            orderStatus.setServer(order.getServer().getLastName() + " " + order.getServer().getFirstName());
            orderStatus.setDate(convertDateToString(order.getDate()));
            orderStatus.setTotal(getTotal(order));
            result.add(orderStatus);
        }
        return result;
    }


    public List<Order> getAllByDate(String date) throws ParseException {
        Date startDate = convertStringToDate(date);
        Date endDate = getEndOfDay(startDate);
        return orderRepository.findOrderByDate(startDate, endDate);
    }

    private List<OrderProduct> saveOrderProducts(Order order, List<OrderProductDTO> orderProductDtoList) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        Map<Long, Integer> orderProductMap = getProductQuantityMap(orderProductDtoList);
        prepareOrderProductsForUpdate(orderProducts, order, orderProductMap);
        List<Product> products = productService.getProductsById(orderProductMap.keySet());
        List<Long> updatedIds = orderProducts.stream().map(orderProduct -> orderProduct.getProduct().getId()).collect(Collectors.toList());

        for (Product product : products) {
            if (!updatedIds.contains(product.getId())) {
                OrderProduct orderProduct = new OrderProduct(product, orderProductMap.get(product.getId()));
                orderProducts.add(orderProduct);
            }
        }

        if (!orderProducts.isEmpty()) {
            updateIngredients(order.getProducts(), orderProducts);
            return orderProductService.saveAll(orderProducts);
        }
        return new ArrayList<>();
    }

    private void prepareOrderProductsForUpdate(List<OrderProduct> productsToSave, Order order, Map<Long, Integer> orderProductMap) {
        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            for (OrderProduct orderProduct : order.getProducts()) {
                if (orderProductMap.containsKey(orderProduct.getProduct().getId())) {
                    Integer quantity = orderProductMap.get(orderProduct.getProduct().getId());
                    //determine differneec for ingredients
                    orderProduct.setQuantity(quantity);
                    productsToSave.add(orderProduct);
                }
            }
        }
    }

    private void updateIngredients(List<OrderProduct> existingProducts, List<OrderProduct> productsToSave) {
        Map<Long, Double> existingProductIngredients = getUsedIngredientsMap(existingProducts);
        Map<Long, Double> useIngredientsMap = getUsedIngredientsMap(productsToSave);
        List<Ingredient> ingredients = ingredientService.getIngredientByIds(useIngredientsMap.keySet());
        for (Ingredient ingredient : ingredients) {
            double oldUsedQuantity = 0;
            if (existingProductIngredients.containsKey(ingredient.getId())) {
                oldUsedQuantity = existingProductIngredients.get(ingredient.getId());
            }
            double toUseQuantity;
            if (oldUsedQuantity != 0) {
                toUseQuantity = useIngredientsMap.get(ingredient.getId()) - oldUsedQuantity;
            } else {
                toUseQuantity = useIngredientsMap.get(ingredient.getId());
            }
            double availableQuantity = ingredient.getQuantity();
            ingredient.setQuantity(availableQuantity - toUseQuantity);
        }
        ingredientService.saveIngredients(ingredients);
    }

    private Map<Long, Double> getUsedIngredientsMap(List<OrderProduct> products) {
        Map<Long, Double> useIngredientsMap = new HashMap<>();
        if (products == null || products.isEmpty()) {
            return useIngredientsMap;
        }

        for (OrderProduct orderProduct : products) {
            int quantity = orderProduct.getQuantity();
            List<ProductIngredient> productIngredients = orderProduct.getProduct().getIngredients();
            for (ProductIngredient productIngredient : productIngredients) {
                if (useIngredientsMap.containsKey(productIngredient.getIngredient().getId())) {
                    double usedQuantity = useIngredientsMap.get(productIngredient.getIngredient().getId());
                    double quantityToUse = productIngredient.getQuantity() * quantity;
                    useIngredientsMap.put(productIngredient.getIngredient().getId(), usedQuantity + quantityToUse);
                } else {
                    double quantityToUse = productIngredient.getQuantity() * quantity;
                    useIngredientsMap.put(productIngredient.getIngredient().getId(), quantityToUse);
                }
            }
        }
        return useIngredientsMap;
    }

    private Map<Long, Integer> getProductQuantityMap(List<OrderProductDTO> orderProductList) {
        Map<Long, Integer> resultMap = new HashMap<>();
        for (OrderProductDTO orderProductDTO : orderProductList) {
            if (orderProductDTO.getQuantity() > 0) {
                resultMap.put(orderProductDTO.getId(), orderProductDTO.getQuantity());
            }
        }
        return resultMap;
    }

    private Order getOrderToSave(Long id) {
        if (id != null) {
            return orderRepository.findById(id).orElse(new Order());
        }
        return new Order();
    }

    private Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date result = dateFormat.parse(date);
        return getStartOfDay(result);
    }

    private String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String resultDate = dateFormat.format(date);
        return resultDate;
    }

    private double getTotal(Order order) {
        double total = 0;
        for (OrderProduct product : order.getProducts()) {
            total = total + product.getQuantity() * product.getProduct().getPrice();
        }
        return total;
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfDay(date));
        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date changeDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 6);
        return calendar.getTime();
    }
}
