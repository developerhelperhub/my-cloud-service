import React from 'react';

import ApiRepo from '../components/ApiRepo';

class AppApiRepo extends ApiRepo {

    static getBasePath() {
        return "http://localhost:8085";
    }

    static getToken() {
        return 'Bearer ' + JSON.parse(localStorage.getItem('authentiction')).access_token;
    }

    static getBasicToken() {
        return 'Basic bXktY2xvdWQtaWRlbnRpdHk6VmtacHp6S2EzdU1xNHZxZw==';
    }

}

export default AppApiRepo;