import React from 'react';

class Main extends React.Component {
    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <div class="app-main">
                {childrens}
            </div>
        );
    }
}

export default Main;