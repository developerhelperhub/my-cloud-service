import React from 'react';
import { Route, Switch, Link, BrowserRouter as Router } from 'react-router-dom'
import Signin from './registration/signin/Signin'
import Signup from './registration/signup/Signup'

import './Home.css'


class Home extends React.Component {
    render() {
        

        return (
            <div class="container-fluid home-page">
                <div class="d-flex justify-content-center h-100">
                    <Router>
                        <Switch>
                            <Route exact path="/login" component={Signin} />
                            <Route exact path="/signin" component={Signin} />
                            <Route exact path="/signup" component={Signup} />
                        </Switch>
                    </Router>
                </div>
            </div>
        );
    }
}

export default Home;