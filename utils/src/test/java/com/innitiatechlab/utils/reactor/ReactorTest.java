package com.innitiatechlab.utils.reactor;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;

public class ReactorTest {

  @Test
  void testFlux() {
    List<Integer> list = new ArrayList<>();
    Flux.just(1, 3, 4, 5, 6, 7, 8)
      .filter(n -> n % 2 == 0)
      .map(n -> n * 2)
      .log()
      .subscribe(n -> list.add(n));

    assertThat(list).containsExactly(8,12,16);
  }

  @Test
  void testFluxBlocking() {
    List<Integer> list = Flux.just(1, 3, 4, 5, 6, 7, 8)
      .filter(n -> n % 2 == 0)
      .map(n -> n * 2)
      .log()
      .collectList().block();

    assertThat(list).containsExactly(8, 12, 16);
  }
}
