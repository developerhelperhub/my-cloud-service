import React from 'react';

import MainContext from './MainContext'

class Main extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            isAuthenticated: false
        };

        this.setAuthenticated = this.setAuthenticated.bind(this);
    }

    setAuthenticated(value) {

        this.setState({
            isAuthenticated: value
        })

    }

    render() {

        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <MainContext.Provider value={
                {
                    isAuthenticated: this.state.isAuthenticated,
                    setAuthenticated: this.setAuthenticated
                }}>

                <div class="app-main">
                    {childrens}
                </div>

            </MainContext.Provider>

        );
    }
}

export default Main;