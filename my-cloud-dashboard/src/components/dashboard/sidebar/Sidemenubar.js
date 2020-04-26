import React from 'react';

class Sidemenubar extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <nav class="navbar bg-light">
                <ul class="navbar-nav menu-bar">

                    {childrens}

                </ul>
            </nav>
        );
    }
}

export default Sidemenubar;