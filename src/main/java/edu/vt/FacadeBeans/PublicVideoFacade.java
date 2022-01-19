/*
 * Created by Mayur Dhepe on 2021.11.26
 * Copyright © 2021 Mayur Dhepe. All rights reserved.
 */

package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.PublicVideo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class PublicVideoFacade extends AbstractFacade<PublicVideo> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "Videos-DhepePU")
    private EntityManager entityManager;

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public PublicVideoFacade() {
        super(PublicVideo.class);
    }

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    // Return the list of object references of all those videos where
    // title contains the searchString entered by the user.
    public List<PublicVideo> titleQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the title
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.title LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    // Return the list of object references of all those videos where
    // Description contains the searchString entered by the user.
    public List<PublicVideo> descriptionQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the stock ticker name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.description LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    // Return the list of object references of all those videos where
    // Category contains the searchString entered by the user.
    public List<PublicVideo> categoryQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the business Sector name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.category LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    // Return the list of object references of all those videos where Title OR
    // Description OR Category contains the searchString entered by the user.
    public List<PublicVideo> allQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in Title OR
        // Description OR Category
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.title LIKE :searchString OR c.description LIKE :searchString OR c.category LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    // title contains titleQ AND category contains categoryQ
    public List<PublicVideo> type2SearchQuery(String title, String category) {

        title = "%" + title + "%";
        category = "%" + category + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.title LIKE :title AND c.category LIKE :category")
                .setParameter("title", title)
                .setParameter("category", category)
                .getResultList();
    }

    // description name contains descriptionQ AND category contains categoryQ
    public List<PublicVideo> type3SearchQuery(String description, String category) {

        description = "%" + description + "%";
        category = "%" + category + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.description LIKE :description AND c.category LIKE :category")
                .setParameter("description", description)
                .setParameter("category", category)
                .getResultList();
    }

    // Video date published is between fromDate and toDate
    public List<PublicVideo> type4SearchQuery(Date from, Date to) {

        return getEntityManager().createQuery(
                        "SELECT c FROM PublicVideo c WHERE c.datePublished BETWEEN :fromDate AND :toDate")
                .setParameter("fromDate", from)
                .setParameter("toDate", to)
                .getResultList();
    }

}
