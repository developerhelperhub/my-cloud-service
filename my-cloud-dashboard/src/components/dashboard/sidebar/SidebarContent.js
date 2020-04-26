import React from 'react';

class SidebarContent extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (<div class="sidebar-content">

            {childrens}

        </div>);
    }
}

export default SidebarContent;