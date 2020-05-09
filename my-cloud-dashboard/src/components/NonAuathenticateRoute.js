import React from 'react';
import { Route, Redirect } from 'react-router-dom';

export const NonAuathenticateRoute = ({ component: Component, ...rest }) => (
    
    <Route {...rest} render={props => (

        localStorage.getItem('authentiction') == null ? <Component {...props} />

            : <Redirect to={{
                pathname: '/',
                state: {
                    from: props.location
                }
            }} />
    )} />
)