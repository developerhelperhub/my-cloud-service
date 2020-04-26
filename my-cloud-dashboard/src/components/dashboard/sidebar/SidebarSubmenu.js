import React from 'react';

class SidebarSubmenu extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <div class="sidebar-submenu">
                <ul>
                    {childrens}
                </ul>
            </div>
        );
    }
}

export default SidebarSubmenu;