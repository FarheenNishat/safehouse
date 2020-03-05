package com.safehouse.almasecure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LicenseResponse {

    @SerializedName("data")
    private Data data;
    @SerializedName("result")
    private String result;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public class Data {

        @SerializedName("total_count")
        @Expose
        private int totalCount;
        @SerializedName("used_count")
        @Expose
        private int usedCount;
        @SerializedName("results")
        @Expose
        private List<Result> results = null;


        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getUsedCount() {
            return usedCount;
        }

        public void setUsedCount(Integer usedCount) {
            this.usedCount = usedCount;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

    }

    public class AvailableLicense {

        @SerializedName("license_hash")
        private String licenseHash;
        @SerializedName("expiry_date")
        private String expiryDate;
        @SerializedName("id")

        private int id;

        public String getLicenseHash() {
            return licenseHash;
        }

        public void setLicenseHash(String licenseHash) {
            this.licenseHash = licenseHash;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public class Result {

        @SerializedName("license_type")
        private String licenseType;
        @SerializedName("total_count")
        private int totalCount;
        @SerializedName("used_count")
        private int usedCount;
        @SerializedName("available_licenses")
        private List<AvailableLicense> availableLicenses = null;
        @SerializedName("used_licenses")
        private List<UsedLicense> usedLicenses = null;

        public String getLicenseType() {
            return licenseType;
        }

        public void setLicenseType(String licenseType) {
            this.licenseType = licenseType;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public int getUsedCount() {
            return usedCount;
        }

        public void setUsedCount(Integer usedCount) {
            this.usedCount = usedCount;
        }

        public List<AvailableLicense> getAvailableLicenses() {
            return availableLicenses;
        }

        public void setAvailableLicenses(List<AvailableLicense> availableLicenses) {
            this.availableLicenses = availableLicenses;
        }

        public List<UsedLicense> getUsedLicenses() {
            return usedLicenses;
        }

        public void setUsedLicenses(List<UsedLicense> usedLicenses) {
            this.usedLicenses = usedLicenses;
        }

    }

    public class UsedLicense {

        @SerializedName("license_hash")
        private String licenseHash;
        @SerializedName("expiry_date")
        private String expiryDate;
        @SerializedName("id")
        private int id;

        public String getLicenseHash() {
            return licenseHash;
        }

        public void setLicenseHash(String licenseHash) {
            this.licenseHash = licenseHash;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

    }
}
