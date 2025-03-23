package com.courier.resourceservice.objects.criteria;

import org.springframework.data.jpa.domain.Specification;

import com.courier.resourceservice.objects.entity.Contact;
import com.courier.resourceservice.objects.entity.Office;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ContactCriteria {

  public static Specification<Contact> containsText(String text) {
    return (Root<Contact> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      String likePattern = "%" + text.toLowerCase() + "%";

      Predicate fullNamePredicate = cb.like(cb.lower(root.get("fullName")), likePattern);
      Predicate phoneNumberPredicate = cb.like(cb.lower(root.get("phoneNumber")), likePattern);

      Join<Contact, Office> officeJoin = root.join("office", JoinType.LEFT);
      Predicate officeNamePredicate = cb.like(cb.lower(officeJoin.get("name")), likePattern);

      Predicate enabledPredicate = cb.isTrue(root.get("enabled"));

      return cb.and(
          enabledPredicate, cb.or(fullNamePredicate, phoneNumberPredicate, officeNamePredicate));
    };
  }
}
