package com.innitiatechlab.ms.core.product.services;

import com.innitiatechlab.api.core.product.Product;
import com.innitiatechlab.api.core.product.ProductService;
import com.innitiatechlab.api.exceptions.InvalidInputException;
import com.innitiatechlab.api.exceptions.NotFoundException;
import com.innitiatechlab.ms.core.product.persistence.ProductEntity;
import com.innitiatechlab.ms.core.product.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.innitiatechlab.utils.http.ServiceUtils;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@RestController
public class ProductServiceImpl implements ProductService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

  private final ServiceUtils serviceUtil;
  private final ProductRepository repository;
  private final ProductMapper mapper;

  @Autowired
  public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtils serviceUtil) {
    this.serviceUtil = serviceUtil;
    this.repository = repository;
    this.mapper = mapper;

  }

  @Override
  public Mono<Product> createProduct(Product body) {

    if (body.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    }

    ProductEntity entity = mapper.apiToEntity(body);
    Mono<Product> newEntity = repository.save(entity)
      .log(LOG.getName(), FINE)
      .onErrorMap(
        DuplicateKeyException.class,
        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId())
      )
      .map(e -> mapper.entityToApi(e));

    return newEntity;
  }

  @Override
  public Mono<Product> getProduct(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    LOG.info("Will get product info for id={}", productId);

    return repository.findByProductId(productId)
      .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
      .log(LOG.getName(), FINE)
      .map(e -> mapper.entityToApi(e))
      .map(e -> setServiceAddress(e));
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);

    return repository.findByProductId(productId).log(LOG.getName(), FINE).map(e -> repository.delete(e)).flatMap(e -> e);
  }

  private Product setServiceAddress(Product e) {
    e.setServiceAddress(serviceUtil.getServiceAddress());
    return e;
  }
}
