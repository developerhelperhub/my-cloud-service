import React from 'react';

class SidebarHeader extends React.Component {

    render() {
        return (
            <div class="sidebar-heading">{this.props.children}</div>
        );
    }
}

export default SidebarHeader;