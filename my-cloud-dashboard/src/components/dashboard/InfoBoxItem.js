import React from 'react';
import PropTypes from 'prop-types';

import './Dashboard.css'

class InfoBoxItem extends React.Component {


    render() {
        var childrens;

        if (this.props.children !=null && this.props.children.type == PropTypes.object) {

            childrens = React.Children.map(this.props.children, children =>
                React.cloneElement(children)
            );

        } else {
            childrens = this.props.children;
        }


        return (
            <div class="d-flex flex-row bd-highlight ml-3 mr-3 border-bottom item-body">
                <div class="d-flex flex-row bd-highlight">
                    <div class="bd-highlight item-label">{this.props.label}</div>
                    <div class="bd-highlight">{childrens}</div>
                </div>
            </div>
        );
    }
}

export default InfoBoxItem;