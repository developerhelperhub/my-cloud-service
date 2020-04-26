import React from 'react';

class SidebarMenuitem extends React.Component {

    render() {
        var badge = "";
        var font = "";

        if (this.props.badge != null) {
            badge = <span class={"badge badge-pill " + this.props.badge.type}>{this.props.badge.label}</span>;
        }

        if (this.props.font != null) {
            font = <i class={this.props.font}></i>;
        }

        var href = "#";
        if(this.props.href != null) {
            href = this.props.href;
        }

        return (<li>
            <a href={href}>
                {font}
                <span>{this.props.label}</span>
                {badge}
            </a>
        </li>);
    }
}

export default SidebarMenuitem;