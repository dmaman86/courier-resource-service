package com.courier.officeservice.objects.criteria;

import org.springframework.data.jpa.domain.Specification;

import com.courier.officeservice.objects.entity.Branch;
import com.courier.officeservice.objects.entity.Office;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BranchCriteria {

  public static Specification<Branch> containsText(String text) {
    return (Root<Branch> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      String likePattern = "%" + text.toLowerCase() + "%";

      Predicate cityPredicate = cb.like(cb.lower(root.get("city")), likePattern);
      Predicate addressPredicate = cb.like(cb.lower(root.get("address")), likePattern);

      Join<Branch, Office> officeJoin = root.join("office", JoinType.LEFT);
      Predicate officeNamePredicate = cb.like(cb.lower(officeJoin.get("name")), likePattern);

      Predicate enabledPredicate = cb.isTrue(root.get("enabled"));

      return cb.and(enabledPredicate, cb.or(cityPredicate, addressPredicate, officeNamePredicate));
    };
  }
}
