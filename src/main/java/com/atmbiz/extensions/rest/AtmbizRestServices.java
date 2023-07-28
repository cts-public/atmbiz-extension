/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.atmbiz.extensions.rest;

import com.atmbiz.extensions.AtmbizExtension;
import com.atmbiz.extensions.dao.*;
import com.atmbiz.extensions.utils.HmacValidator;
import com.atmbiz.extensions.utils.TransformUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

/**
 * This class represents the RESTful API for interacting with ATMBiz's services.
 * It provides services for getting terminals, selling crypto etc.
 */
@Path("/")
public class AtmbizRestServices {
    private static final Logger log = LoggerFactory.getLogger(AtmbizRestServices.class);
    private static final String API_SECRET = AtmbizExtension.getExtensionContext().getConfigProperty("atmbiz", "API_SECRET", "DEFAULT");

    /**
     * Fetches terminals information from the ATM business extension.
     * If an API key is provided, it filters the terminals that share the same Morphis API access.
     *
     * @param morphisApiKey The API key for a specific Morphis API access. Cannot be null.
     * @return a list of terminals in JSON format, or an error message.
     */
    @GET
    @Path("/terminals")
    @Produces(MediaType.APPLICATION_JSON)
    public Response terminals(@HeaderParam("MORPHIS_API_KEY") String morphisApiKey) {
        // API key is mandatory, return an error response if not provided
        if (morphisApiKey == null || morphisApiKey.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST)
                    .entity(new ErrorMessage(HttpServletResponse.SC_BAD_REQUEST, "API key is mandatory"))
                    .build();
        }

        try {
            IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(morphisApiKey, ApiAccessType.MORPHIS);
            if (iApiAccess != null) {
                // If the API key is valid, filter terminals and return them
                return Response.ok(getTerminalsByApiKey(iApiAccess)).build();
            } else {
                // If the API key is not valid, return an Unauthorized response
                return Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .entity(new ErrorMessage(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized response: Invalid API key"))
                        .build();
            }
        } catch (Exception e) {
            log.error("Error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()))
                    .build();
        }
    }

    /**
     * Method helps collect terminals with the same Morphis API Access in CAS
     *
     * @param iApiAccess - Morphis API key
     * @return List<AtmbizTerminal> - a list of terminals that have the same Morphis API Access as the provided key.
     */
    private List<AtmbizTerminal> getTerminalsByApiKey(IApiAccess iApiAccess) {
        Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();
        List<AtmbizTerminal> filteredTerminals = new ArrayList<>();
        terminals.forEach(terminalSerial -> {
            ITerminal terminal = AtmbizExtension.getExtensionContext().findTerminalBySerialNumber(terminalSerial);
            if (terminal != null && !terminal.isDeleted()) {
                filteredTerminals.add(TransformUtils.getAtmbizTerminal(terminal));
            }
        });
        return filteredTerminals;
    }

    /**
     * Performs selling of crypto.
     * @param requestObject Request object for selling crypto.
     * @param hmac HMAC signature for the request.
     * @param morphisApiKey The API key for a specific Morphis API access. Cannot be null.
     * @return Response with the result of the operation, or error message if any exception occurs.
     */
    @POST
    @Path("/sell_crypto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellCrypto(AtmbizSellCryptoRequest requestObject, @HeaderParam("X-SIGNATURE") String hmac,  @HeaderParam("MORPHIS_API_KEY") String morphisApiKey) {
        // Validate if serialNumber in request object is valid against given api_key
        boolean isValidSerialNumber = validateSerialNumber(morphisApiKey, requestObject.getSerialNumber());
        if (!isValidSerialNumber) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid Serial Number for the given API Key")).build();
        }

        // Use the JSON string as the input for your HMAC calculation
        ObjectMapper mapper = new ObjectMapper();
        String data;
        try {
            data = mapper.writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing JSON", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()))
                    .build();
        }

        log.info("Received sellCrypto request with data: {}", data);

        // Validate HMAC
        if (!HmacValidator.isValid(hmac, data, API_SECRET)) {
            log.error("Unauthorized response: HMAC validation failed");
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("Unauthorized response: HMAC validation failed").build();
        }

        // Validate request object
        if (requestObject.getSerialNumber() == null || requestObject.getFiatAmount() == null || requestObject.getFiatCurrency() == null || requestObject.getCryptoAmount() == null || requestObject.getCryptoCurrency() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), "Missing properties!")).build();
        }

        // Create Identity
        IIdentity identity;
        try {
            identity = AtmbizExtension.getExtensionContext().addIdentity(requestObject.getFiatCurrency(), requestObject.getSerialNumber(), null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), "note", 4, BigDecimal.ZERO, BigDecimal.ZERO, new Date(), null);
        } catch (Throwable e) {
            log.error("Error occurred while creating identity", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error during identity creation:" + e.getMessage()))
                    .build();
        }
        // Add phone and email
        try {
            if (requestObject.getPhoneNumber() != null)
                AtmbizExtension.getExtensionContext().addIdentityPiece(identity.getPublicId(), new IdentityPiece(IIdentityPiece.TYPE_CELLPHONE, null, requestObject.getPhoneNumber()));
            if (requestObject.getEmail() != null)
                AtmbizExtension.getExtensionContext().addIdentityPiece(identity.getPublicId(), new IdentityPiece(IIdentityPiece.TYPE_EMAIL, requestObject.getEmail(), null));
        } catch (Throwable e) {
            log.error("Error occurred while updating identity", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error during identity update:" + e.getMessage()))
                    .build();
        }

        BigDecimal avaiableCash = AtmbizExtension.getExtensionContext().calculateCashAvailableForSell(requestObject.getSerialNumber(), requestObject.getFiatCurrency());
        log.info("Available cash is {}", avaiableCash);

        if (avaiableCash.compareTo(requestObject.getFiatAmount()) < 0) {
            log.error("Not enough cash in ATM, available cash is {}", avaiableCash);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), "There is no enough cash in ATM, available cash is " + avaiableCash))
                    .build();
        }

        try {
            log.info("Successfully sold crypto");
            return Response.ok(new SellCryptoResponse(0, AtmbizExtension.getExtensionContext().sellCrypto(requestObject.getSerialNumber(), requestObject.getFiatAmount(), requestObject.getFiatCurrency(), requestObject.getCryptoAmount(), requestObject.getCryptoCurrency(), identity.getPublicId(), requestObject.getDiscountCode()))).build();
        } catch (Throwable e) {
            log.error("Error occurred while selling crypto", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()))
                    .build();
        }
    }

    private boolean validateSerialNumber(String apiKey, String serialNumber) {
        IApiAccess iApiAccess = AtmbizExtension.getExtensionContext().getAPIAccessByKey(apiKey, ApiAccessType.MORPHIS);
        if (iApiAccess != null) {
            Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();
            return terminals.contains(serialNumber);
        }
        return false;
    }
}