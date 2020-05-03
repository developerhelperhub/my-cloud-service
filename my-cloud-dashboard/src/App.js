import React from 'react';
import Main from './components/Main'
import Home from './home/Home'
import { Route, Switch, Link, BrowserRouter as Router } from 'react-router-dom'
import DashboardPage from './pages/DashboarPage'


import './App.css'

function App() {

  return (
    <Main>
        
        <Router>
          <Switch>
            <Route exact path="/login" component={Home} />
            <Route exact path="/signin" component={Home} />
            <Route exact path="/signup" component={Home} />
            <Route path="/" component={DashboardPage} />
          </Switch>
        </Router>

    </Main>
  );
}

export default App;