package com.atmbiz.extensions.dao;

import com.generalbytes.batm.server.extensions.*;

import java.util.Date;

public class IdentityPiece implements IIdentityPiece {

    public IdentityPiece(int pieceType,String email, String phoneNumber){
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pieceType = pieceType;
    }

    private int pieceType;

    private String email;

    private String phoneNumber;

    @Override
    public int getPieceType() {
        return pieceType;
    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public String getEmailAddress() {
        return email;
    }

    @Override
    public String getFirstname() {
        return null;
    }

    @Override
    public String getLastname() {
        return null;
    }

    @Override
    public String getContactAddress() {
        return null;
    }

    @Override
    public String getContactCity() {
        return null;
    }

    @Override
    public String getContactCountry() {
        return null;
    }

    @Override
    public String getContactCountryIso2() {
        return null;
    }

    @Override
    public String getContactProvince() {
        return null;
    }

    @Override
    public String getContactZIP() {
        return null;
    }

    @Override
    public String getIssuingJurisdictionCountry() {
        return null;
    }

    @Override
    public String getIssuingJurisdictionProvince() {
        return null;
    }

    @Override
    public String getIdCardNumber() {
        return null;
    }

    @Override
    public IPerson getCreatedBy() {
        return null;
    }

    @Override
    public Date getDocumentValidTo() {
        return null;
    }

    @Override
    public Integer getDocumentType() {
        return null;
    }

    @Override
    public Date getDateOfBirth() {
        return null;
    }

    @Override
    public String getOccupation() {
        return null;
    }

    @Override
    public String getSSN() {
        return null;
    }
}
