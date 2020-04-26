import React from 'react';

class SidemenubarHeader extends React.Component {

    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        return (
            <li class="nav-item header">
                <span>
                    {this.props.children}
                </span>
            </li>
        );
    }
}

export default SidemenubarHeader;