package com.innitiatechlab.ms.core.review.services;

import com.innitiatechlab.api.core.review.Review;
import com.innitiatechlab.api.core.review.ReviewService;
import com.innitiatechlab.api.exceptions.InvalidInputException;
import com.innitiatechlab.ms.core.review.persistence.ReviewEntity;
import com.innitiatechlab.ms.core.review.persistence.ReviewRepository;
import com.innitiatechlab.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ReviewServiceImpl implements ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

  private final ReviewRepository repository;

  private final ReviewMapper mapper;

  private final ServiceUtils serviceUtil;

  @Autowired
  public ReviewServiceImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtils serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Review createReview(Review body) {
    try {
      ReviewEntity entity = mapper.apiToEntity(body);
      ReviewEntity newEntity = repository.save(entity);

      LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return mapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
    }
  }

  @Override
  public List<Review> getReviews(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    List<ReviewEntity> entityList = repository.findByProductId(productId);
    List<Review> list = mapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getReviews: response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteReviews(int productId) {
    LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}
