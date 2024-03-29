package com.innitiatechlab.ms.core.recommendation.services;

import com.innitiatechlab.api.core.recommendation.Recommendation;
import com.innitiatechlab.api.core.recommendation.RecommendationService;
import com.innitiatechlab.api.exceptions.InvalidInputException;
import com.innitiatechlab.ms.core.recommendation.persistence.RecommendationEntity;
import com.innitiatechlab.ms.core.recommendation.persistence.RecommendationRepository;
import com.innitiatechlab.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final ServiceUtils serviceUtil;
  private final RecommendationMapper mapper;
  private final RecommendationRepository repository;

  @Autowired
  public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtils serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {
    try {
      RecommendationEntity entity = mapper.apiToEntity(body);
      RecommendationEntity newEntity = repository.save(entity);

      LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
      return mapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
    }
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    List<RecommendationEntity> entityList = repository.findByProductId(productId);
    List<Recommendation> list = mapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getRecommendations: response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteRecommendations(int productId) {
    LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}
