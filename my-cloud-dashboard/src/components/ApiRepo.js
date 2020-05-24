import React from 'react';
import { NativeEventSource, EventSourcePolyfill } from 'event-source-polyfill';

class ApiRepo extends React.Component {

    static getBasePath() {
        return "-";
    }

    static getToken() {
        return "-"
    }

    static getGenerateUrl(endPoint) {
        return this.getBasePath() + endPoint;
    }

    static eventSource(endPoint, headers, onmessage, onerror) {
        const evtSource = new EventSourcePolyfill(this.getGenerateUrl(endPoint),
            {
                headers: headers
            });

        evtSource.onmessage = function (event) {
            onmessage(event);
        }

        evtSource.onerror = function (err) {
            if (err.error != undefined) {
                onerror(err);
            }
        };

        return evtSource;
    }

    static async fetch(endPoint, method, headers, body) {

        let formBody = [];
        var isFormUrlEncoded = false;
        for (let head in headers) {
            if (head == "Content-Type" && headers[head].includes("x-www-form-urlencoded")) {
                isFormUrlEncoded = true;
            }
        }
        if (isFormUrlEncoded) {
            for (let property in body) {
                let encodedKey = encodeURIComponent(property);
                let encodedValue = encodeURIComponent(body[property]);
                formBody.push(encodedKey + "=" + encodedValue);
            }
            formBody = formBody.join("&");
        }
        else {
            formBody = JSON.stringify(body);
        }
        const requestOptions = {
            method: method,
            headers: headers,
            body: formBody
        };
        const response = await fetch(this.getGenerateUrl(endPoint), requestOptions)
            .then(response => Promise.all([response.status, response.json()]))
            .then(response => {
                const status = response[0];
                var data = response[1];
                var errorMessage = data.error_description != undefined ? data.error_description : data.error;
                const res = {
                    status: status,
                    errorMessage: errorMessage,
                    data: data
                };
                return res;
            }).catch(error => {
                console.log(error);
                const res = {
                    status: 0,
                    errorMessage: 'Server can\'t be connected!',
                    data: ''
                };
                return res;
            });
        return response;
    }
}

export default ApiRepo;