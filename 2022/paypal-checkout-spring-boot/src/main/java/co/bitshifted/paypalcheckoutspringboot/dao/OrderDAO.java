package co.bitshifted.paypalcheckoutspringboot.dao;

import org.springframework.data.repository.CrudRepository;

public interface OrderDAO extends CrudRepository<Order, Long> {

    Order findByPaypalOrderId(String paypalOrderId);
}
