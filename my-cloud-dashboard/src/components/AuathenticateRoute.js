import React from 'react';
import { Route, Redirect } from 'react-router-dom';

export const AuathenticateRoute = ({ component: Component, ...rest }) => (
    
    <Route {...rest} render={props => (

        localStorage.getItem('authentiction') != null ? <Component {...props} />

            : <Redirect to={{
                pathname: '/login',
                state: {
                    from: props.location
                }
            }} />
    )} />
)