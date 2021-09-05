package com.costa.luiz.reactive.airbnb.infrastructure.review;

import com.costa.luiz.reactive.airbnb.model.review.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewBlockRepository extends MongoRepository<Review, String> {
}
