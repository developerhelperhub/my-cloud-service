import React from 'react';

class SidebarDropdown extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        var badge = "";

        if (this.props.badge != null) {
            badge = <span class={"badge badge-pill " + this.props.badge.type}>{this.props.badge.label}</span>;
        }

        return (
            <li class="nav-item sidebar-dropdown">
                <a href="#">
                    <i class={this.props.font}></i>
                    <span>{this.props.title}</span>

                    {badge}

                </a>

                {childrens}
                
            </li>
        );
    }
}

export default SidebarDropdown;