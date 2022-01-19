/*
 * Created by Mayur Dhepe on 2021.11.26
 * Copyright © 2021 Mayur Dhepe. All rights reserved.
 */

package edu.vt.controllers;

import edu.vt.EntityBeans.PublicVideo;
import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserVideo;
import edu.vt.FacadeBeans.PublicVideoFacade;
import edu.vt.FacadeBeans.UserVideoFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.globals.Methods;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("userVideoController")
@SessionScoped

public class UserVideoController implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
    */
    private UserVideo selected;
    // 'listOfUserFiles' is a List containing the object references of User File objects
    private List<UserVideo> listOfUserVideos = null;
    // Selected row number in p:dataTable in List.xhtml
    private String selectedRowNumber = "0";

    private String idOfVideoToPlay;

    @EJB
    private UserVideoFacade userVideoFacade;

    @EJB
    private PublicVideoFacade publicVideoFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public UserVideo getSelected() {
        return selected;
    }

    public void setSelected(UserVideo selected) {
        this.selected = selected;
    }

    public String getSelectedRowNumber() {
        return selectedRowNumber;
    }

    public void setSelectedRowNumber(String selectedRowNumber) {
        this.selectedRowNumber = selectedRowNumber;
    }

    public String getIdOfVideoToPlay() {
        return idOfVideoToPlay;
    }

    public void setIdOfVideoToPlay(String idOfVideoToPlay) {
        this.idOfVideoToPlay = idOfVideoToPlay;
    }

    /*
    ***************************************************************
    Return the List of User Files that Belong to the Signed-In User
    ***************************************************************
     */
    public List<UserVideo> getListOfUserVideos() {
        if (listOfUserVideos == null) {

            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            User signedInUser = (User) sessionMap.get("user");

            // Obtain the database primary key of the signedInUser object
            Integer primaryKey = signedInUser.getId();

            // Obtain only those files from the database that belong to the signed-in user
            listOfUserVideos = userVideoFacade.findUserVideosByUserPrimaryKey(primaryKey);
        }
        return listOfUserVideos;
    }

    /*
    ================
    Instance Methods
    ================
     */
    public void unselect() {
        listOfUserVideos = null;
        selected = null;
    }

    public String cancel() {
        // Unselect previously selected Video object if any
        selected = null;
        return "/userVideo/List?faces-redirect=true";
    }

    /*
      *****************************
      Prepare to Create a New Video
      *****************************
      */
    public void prepareCreate() {
        /*
        Instantiate a new Video object and store its object reference into
        instance variable 'selected'. The Video class is defined in Video.java
         */
        selected = new UserVideo();
    }

    // The constants CREATE, DELETE and UPDATE are defined in JsfUtil.java

    /*
    **********************
    Create a New User File
    **********************
     */
    public void create() {
        Methods.preserveMessages();

        User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");

        // user is the object reference of the signed-in User object
        selected.setUserId(user);

        persist(PersistAction.CREATE, "User Video was Successfully Created.");

        if (!JsfUtil.isValidationFailed()) {
            selected = null;            // Remove selection
            listOfUserVideos = null;     // Invalidate listOfUserFiles to trigger re-query.
        }
    }

    // We do not allow update of a user file.
    public void update() {
        Methods.preserveMessages();

        persist(PersistAction.UPDATE, "User Video was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfUserVideos = null;    // Invalidate listOfVideos to trigger re-query.
        }
    }

    /*
   ***************************************
   DELETE Selected Video from the Database
   ***************************************
    */
    public void destroy() {
        Methods.preserveMessages();

        persist(PersistAction.DELETE, "User Video was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;        // Remove selection
            listOfUserVideos = null;    // Invalidate listOfVideos to trigger re-query.
        }
    }

    /*
     ****************************************************************************
     *   Perform CREATE, EDIT (UPDATE), and DELETE Operations in the Database   *
     ****************************************************************************
     */

    /**
     * @param persistAction  refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {

        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).
                    
                     UserFileFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    userVideoFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove method performs the DELETE operation in the database.
                    
                     UserFileFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    userVideoFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);

            } catch (EJBException ex) {

                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, "A Persistence Error Occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A Persistence Error Occurred!");
            }
        }
    }


    /*
    =========================
    Delete Selected User Video
    =========================
     */
    public String deleteSelectedUserVideo() {

        UserVideo userVideoToDelete = selected;
        /*
        We need to preserve the messages since we will redirect to show a
        different JSF page after successful deletion of the user file.
         */
        Methods.preserveMessages();

        if (userVideoToDelete == null) {
            Methods.showMessage("Fatal Error", "No Video Selected!", "You do not have a video to delete!");
            return "";
        } else {
            // Delete the file from CloudStorage/FileStorage

            // Delete the user file record from the database
            userVideoFacade.remove(userVideoToDelete);
            // UserFileFacade inherits the remove() method from AbstractFacade

            Methods.showMessage("Information", "Success!", "Selected Video is Successfully Deleted!");

            // See method below
            refreshVideosList();

            return "/userVideo/List?faces-redirect=true";

        }
    }

    /*
    ========================
    Refresh User's Video List
    ========================
     */
    public void refreshVideosList() {
        selected = null;            // Remove selection
        listOfUserVideos = null;     // Invalidate listOfUserVideos to trigger re-query.
    }

    /*
     **********************************************************************************************
     *   Share as Public Video - Adds the selected video to the Public list of favorite videos   *
     **********************************************************************************************
     */
    public String shareAsPublicVideo() {

        UserVideo videoToPublish = selected;

        PublicVideo publicVideo = new PublicVideo();

        publicVideo.setTitle(videoToPublish.getTitle());
        publicVideo.setDatePublished(videoToPublish.getDatePublished());
        publicVideo.setCategory(videoToPublish.getCategory());
        publicVideo.setYoutubeVideoId(videoToPublish.getYoutubeVideoId());
        publicVideo.setDuration(videoToPublish.getDuration());
        publicVideo.setDescription(videoToPublish.getDescription());

        /*
        We need to preserve the messages since we will redirect to show a
        different JSF page after successful deletion of the user file.
         */
        Methods.preserveMessages();

        if (videoToPublish == null) {
            Methods.showMessage("Fatal Error", "No File Selected!", "You do not have a video to publish!");
            return "";
        } else {

            // Delete the user file record from the database
            publicVideoFacade.edit(publicVideo);

            Methods.showMessage("Information", "Success!", "Public Video was successfully created.");

            return "/userVideo/List?faces-redirect=true";

        }
    }

}


