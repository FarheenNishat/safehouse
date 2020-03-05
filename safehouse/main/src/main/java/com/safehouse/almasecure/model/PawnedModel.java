package com.safehouse.almasecure.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PawnedModel {

    private String name;
    private String title;
    private String domain;
    private String breachDate;
    private String addedDate;
    private String modifiedDate;
    private Integer pwnCount;
    private String description;
    private String logoPath;
    private Boolean isVerified;
    private Boolean isFabricated;
    private Boolean isSensitive;
    private Boolean isRetired;
    private Boolean isSpamList;
    private JSONObject object;
    public static PawnedModel fromJsonObject(JSONObject object) throws JSONException {
        PawnedModel mdl = new PawnedModel();

        mdl.name = object.getString("Name");
        mdl.title = object.getString("Title");
        mdl.domain = object.getString("Domain");
        mdl.breachDate = object.getString("BreachDate");
        mdl.addedDate = object.getString("AddedDate");
        mdl.modifiedDate = object.getString("ModifiedDate");
        mdl.pwnCount = object.getInt("PwnCount");
        mdl.description = object.getString("Description");
        mdl.logoPath = object.getString("LogoPath");
        mdl.object = object;
        return mdl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBreachDate() {
        return breachDate;
    }

    public void setBreachDate(String breachDate) {
        this.breachDate = breachDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getPwnCount() {
        return pwnCount;
    }

    public void setPwnCount(Integer pwnCount) {
        this.pwnCount = pwnCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsFabricated() {
        return isFabricated;
    }

    public void setIsFabricated(Boolean isFabricated) {
        this.isFabricated = isFabricated;
    }

    public Boolean getIsSensitive() {
        return isSensitive;
    }

    public void setIsSensitive(Boolean isSensitive) {
        this.isSensitive = isSensitive;
    }

    public Boolean getIsRetired() {
        return isRetired;
    }

    public void setIsRetired(Boolean isRetired) {
        this.isRetired = isRetired;
    }

    public Boolean getIsSpamList() {
        return isSpamList;
    }

    public void setIsSpamList(Boolean isSpamList) {
        this.isSpamList = isSpamList;
    }

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }
}
