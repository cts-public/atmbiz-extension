package com.atmbiz.extensions.dao;

public class AtmbizUpdateTransactionRequest {
        private String status;
        private String errorCode;
        private String remoteTransactionId;
        private Object updateEvent;

        public Object getUpdateEvent() {
            return updateEvent;
        }

        public void setUpdateEvent(Object updateEvent) {
            this.updateEvent = updateEvent;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRemoteTransactionId() {
            return remoteTransactionId;
        }

        public void setRemoteTransactionId(String remoteTransactionId) {
            this.remoteTransactionId = remoteTransactionId;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
}
