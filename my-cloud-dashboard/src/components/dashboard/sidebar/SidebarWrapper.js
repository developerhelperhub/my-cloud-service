import React from 'react';

class SidebarWrapper extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <div class="sidebar-wrapper">

                {childrens}

            </div>

        );
    }
}

export default SidebarWrapper;