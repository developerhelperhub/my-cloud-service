import React from 'react';
import PropTypes from 'prop-types';

class Label extends React.Component {


    render() {
        var childrens;

        if (this.props.children.type == PropTypes.object) {

            childrens = React.Children.map(this.props.children, children =>
                React.cloneElement(children)
            );

        } else {
            childrens = this.props.children;
        }
        
        var label = "";
        
        if (this.props.class != null) {
            label = "label " + this.props.class;
        }

        return (

            <span class={label}>

                {childrens}

            </span>

        );
    }
}

export default Label;