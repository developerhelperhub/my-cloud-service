import React from 'react';

import MainContext from './MainContext'

class Main extends React.Component {

    render() {
        
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <MainContext.Provider value={{ isAuthenticated: false }}>
                
                <div class="app-main">
                    {childrens}
                </div>

            </MainContext.Provider>

        );
    }
}

export default Main;