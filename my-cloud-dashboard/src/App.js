import React from 'react';
import Main from './components/Main'
import { AuathenticateRoute } from './components/AuathenticateRoute'
import { NonAuathenticateRoute } from './components/NonAuathenticateRoute'

import Home from './home/Home'
import { Route, Switch, Link, BrowserRouter as Router } from 'react-router-dom'
import DashboardPage from './pages/DashboarPage'

import './App.css'

function App() {

  return (
    <Main>

      <Router>
        <Switch>

          <Route exact path="/logout" component={Home} />

          <NonAuathenticateRoute exact path="/login" component={Home} />
          <NonAuathenticateRoute exact path="/signin" component={Home} />
          <NonAuathenticateRoute exact path="/signup" component={Home} />

          <AuathenticateRoute path="/" component={DashboardPage} />

        </Switch>
      </Router>

    </Main>
  );
}

export default App;