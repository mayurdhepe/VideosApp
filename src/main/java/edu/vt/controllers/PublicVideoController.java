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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("publicVideoController")
@SessionScoped
public class PublicVideoController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
    */
    private String idOfVideoToPlay;
    private List<PublicVideo> listOfPublicVideos = null;
    private PublicVideo selected;

    private Integer searchType;
    private String searchField, searchString, title, category, description;
    private Date from, to;
    private List<PublicVideo> searchItems = null;

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    PublicVideoFacade bean into the instance variable 'publicVideoFacade' after it is instantiated at runtime.
     */
    @EJB
    private PublicVideoFacade publicVideoFacade;

    @EJB
    private UserVideoFacade userVideoFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public List<PublicVideo> getListOfPublicVideos() {
        if (listOfPublicVideos == null) {
            listOfPublicVideos = publicVideoFacade.findAll();
        }
        return listOfPublicVideos;
    }

    public PublicVideo getSelected() {
        return selected;
    }

    public void setSelected(PublicVideo selected) {
        this.selected = selected;
    }

    public String getIdOfVideoToPlay() {
        return idOfVideoToPlay;
    }

    public void setIdOfVideoToPlay(String idOfVideoToPlay) {
        this.idOfVideoToPlay = idOfVideoToPlay;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    /*
     **************************************
     *   Unselect Selected PublicVideo Object   *
     **************************************
     */
    public void unselect() {
        listOfPublicVideos = null;
        selected = null;
    }

    /*
    ================
    Instance Methods
    ================
    */

    /*
     *************************************
     *   Cancel and Display List.xhtml   *
     *************************************
     */
    public String cancel() {
        // Unselect previously selected PublicVideo object if any
        selected = null;
        return "/publicVideo/List?faces-redirect=true";
    }

    /*
    *****************************
    Prepare to Create a New PublicVideo
    *****************************
    */
    public void prepareCreate() {
        /*
        Instantiate a new PublicVideo object and store its object reference into
        instance variable 'selected'. The PublicVideo class is defined in PublicVideo.java
         */
        selected = new PublicVideo();
    }

    /*
    **********************************
    CREATE a New PublicVideo in the Database
    **********************************
     */
    public void create() {
        Methods.preserveMessages();

        persist(PersistAction.CREATE, "Public Video was Successfully Created.");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfPublicVideos = null;    // Invalidate listOfPublicVideo to trigger re-query.
        }
    }

    /*
    An enum is a special Java type used to define a group of constants.
    The constants CREATE, DELETE and UPDATE are defined as follows in JsfUtil.java

            public enum PersistAction {
                CREATE,
                DELETE,
                UPDATE
            }
     */

    /*
    *************************************
    UPDATE Selected PublicVideo in the Database
    *************************************
     */
    public void update() {
        Methods.preserveMessages();

        persist(PersistAction.UPDATE, "Public Video was Successfully Updated.");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfPublicVideos = null;    // Invalidate listOfPublicVideo to trigger re-query.
        }
    }

    /*
    ***************************************
    DELETE Selected PublicVideo from the Database
    ***************************************
     */
    public void destroy() {
        Methods.preserveMessages();

        persist(PersistAction.DELETE, "Public Video was Successfully Deleted.");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;        // Remove selection
            listOfPublicVideos = null;    // Invalidate listOfPublicVideo to trigger re-query.
        }
    }

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

                     PublicVideoFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    publicVideoFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     PublicVideoFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    publicVideoFacade.remove(selected);
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
                    JsfUtil.addErrorMessage(ex, "A persistence error occurred.");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A persistence error occurred.");
            }
        }
    }

    /*
     **********************************************************************************************
     *   Share as User Video - Adds the selected video to the Signed-In user's list of favorite videos   *
     **********************************************************************************************
     */
    public String shareAsUserVideo() {
        User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user");
        Methods.preserveMessages();

        if (user == null) {
            Methods.showMessage("Information", "Unable to Share!", "To share a video, a user must be signed in!");
            return "";
        }

        PublicVideo publicVideo = selected;

        UserVideo userVideo = new UserVideo();

        userVideo.setTitle(publicVideo.getTitle());
        userVideo.setDatePublished(publicVideo.getDatePublished());
        userVideo.setCategory(publicVideo.getCategory());
        userVideo.setYoutubeVideoId(publicVideo.getYoutubeVideoId());
        userVideo.setDuration(publicVideo.getDuration());
        userVideo.setDescription(publicVideo.getDescription());
        userVideo.setUserId(user);

        userVideoFacade.edit(userVideo);

        Methods.showMessage("Information", "", "User Video was successfully created!");

        return "/publicVideo/List?faces-redirect=true";

    }

    /*
     ******************************************
     *   Display the Search Results JSF Page  *
     ******************************************
     */
    public String search(Integer type) {
        // Set search type given as input parameter
        searchType = type;

        // Unselect previously selected public video if any before showing the search results
        selected = null;

        // Invalidate list of search items to trigger re-query
        searchItems = null;

        return "/publicVideo/SearchResults?faces-redirect=true";
    }

    /*
     **********************************************************************************************
     *   Get Search Items by performing a database search   *
     **********************************************************************************************
     */
    public List<PublicVideo> getSearchItems() {
        if (searchItems == null) {
            switch (searchType) {
                case 1: // Search Type 1
                    switch (searchField) {
                        case "Title":
                            // Return the list of object references of all those videos where
                            // title contains the searchString entered by the user.
                            searchItems = publicVideoFacade.titleQuery(searchString);
                            break;
                        case "Description":
                            // Return the list of object references of all those videos where
                            // Description contains the searchString entered by the user.
                            searchItems = publicVideoFacade.descriptionQuery(searchString);
                            break;
                        case "Category":
                            // Return the list of object references of all those videos where
                            // Category contains the searchString entered by the user.
                            searchItems = publicVideoFacade.categoryQuery(searchString);
                            break;
                        default:
                            // Return the list of object references of all those videos where Title OR
                            // Description OR Category contains the searchString entered by the user.
                            searchItems = publicVideoFacade.allQuery(searchString);
                    }
                    break;
                case 2: // Search Type 2
                    // title contains titleQ AND category contains categoryQ
                    searchItems = publicVideoFacade.type2SearchQuery(title, category);
                    break;
                case 3: // Search Type 3
                    // description name contains descriptionQ AND category contains categoryQ
                    searchItems = publicVideoFacade.type3SearchQuery(description, category);
                    break;
                case 4: // Search Type 4
                    // Video date published is between fromDate and toDate
                    searchItems = publicVideoFacade.type4SearchQuery(from, to);
                    break;
                default:
                    Methods.showMessage("Fatal Error", "Search Type is Out of Range!",
                            "");
            }
        }
        return searchItems;
    }

    public void setSearchItems(List<PublicVideo> searchItems) {
        this.searchItems = searchItems;
    }

}
