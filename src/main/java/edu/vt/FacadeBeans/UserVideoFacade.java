/*
 * Created by Mayur Dhepe on 2021.11.26
 * Copyright © 2021 Mayur Dhepe. All rights reserved.
 */

package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserVideo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class UserVideoFacade extends AbstractFacade<UserVideo> {
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
    public UserVideoFacade() {
        super(UserVideo.class);
    }

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
     *********************
     *   Other Methods   *
     *********************
     */

    // Returns the object reference of the UserFile object whose primary key is id
    public UserVideo getUserVideo(int id) {
        return entityManager.find(UserVideo.class, id);
    }

    // Find User Videos by User ID
    public List<UserVideo> findUserVideosByUserPrimaryKey(Integer primaryKey) {
        /*
        The following @NamedQuery definition is given in UserFile entity class file:
        @NamedQuery(name = "UserFile.findUserFilesByUserId", query = "SELECT u FROM UserFile u WHERE u.userId.id = :userId")

        The following statement obtains the results from the named database query.
         */
        return entityManager.createNamedQuery("UserVideo.findUserVideosByUserId")
                .setParameter("userId", primaryKey)
                .getResultList();
    }

}
