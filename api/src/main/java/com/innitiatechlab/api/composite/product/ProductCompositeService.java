package com.innitiatechlab.api.composite.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "ProductComposite", description = "REST API for composite product information.")
public interface ProductCompositeService {

  /**
   * Sample usage: "curl $HOST:$PORT/product-composite/1".
   *
   * @param productId Id of the product
   * @return the composite product info, if found, else null
   */
  @GetMapping(
    value = "/product-composite/{productId}",
    produces = "application/json")
  Mono<ProductAggregate> getProduct(@PathVariable int productId);

  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/product-composite/1".
   *
   * @param productId Id of the product
   */
  @Operation(
          summary = "${api.product-composite.delete-composite-product.description}",
          description = "${api.product-composite.delete-composite-product.notes}")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
          @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
  })
  @ResponseStatus(HttpStatus.ACCEPTED)
  @DeleteMapping(value = "/product-composite/{productId}")
  Mono<Void> deleteProduct(@PathVariable int productId);

  /**
   * Sample usage, see below.
   *
   * curl -X POST $HOST:$PORT/product-composite \
   *   -H "Content-Type: application/json" --data \
   *   '{"productId":123,"name":"product 123","weight":123}'
   *
   * @param body A JSON representation of the new composite product
   */
  @Operation(
          summary = "${api.product-composite.create-composite-product.description}",
          description = "${api.product-composite.create-composite-product.notes}")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
          @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
  })
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PostMapping(
          value    = "/product-composite",
          consumes = "application/json")
  Mono<Void> createProduct(@RequestBody ProductAggregate body);
}
