package com.example.momobooklet_by_sm.presentation.recordingtransanctions;

public class TempTransData {
    private String customername;
    private String customerphone ;
    private String customerpin ;
    private String transactionamount;
    private Boolean transactiontype;
    private byte[] customersiganature;

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCustomerphone() {
        return customerphone;
    }

    public void setCustomerphone(String customerphone) {
        this.customerphone = customerphone;
    }

    public String getCustomerpin() {
        return customerpin;
    }

    public void setCustomerpin(String customerpin) {
        this.customerpin = customerpin;
    }

    public String getTransactionamount() {
        return transactionamount;
    }

    public void setTransactionamount(String transactionamount) {
        this.transactionamount = transactionamount;
    }

    public Boolean getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(Boolean transactiontype) {
        this.transactiontype = transactiontype;
    }

    public byte[] getCustomersiganature() {
        return customersiganature;
    }

    public void setCustomersiganature(byte[] customersiganature) {
        this.customersiganature = customersiganature;
    }


    public TempTransData(String customername, String customerphone, String customerpin, String transactionamount, Boolean transactiontype, byte[] customersiganature) {
        this.customername = customername;
        this.customerphone = customerphone;
        this.customerpin = customerpin;
        this.transactionamount = transactionamount;
        this.transactiontype = transactiontype;
        this.customersiganature = customersiganature;
    }
}
