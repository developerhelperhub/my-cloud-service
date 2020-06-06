import React from 'react';

import Label from '../../components/dashboard/Label'

import './Dashboard.css'

class InfoBox extends React.Component {


    render() {
        const childrens = React.Children.map(this.props.children, children =>
            React.cloneElement(children)
        );

        var labelHtml = "";

        if (this.props.label != null) {
            var labelClass = "label-warning";

            if (this.props.label.class != null) {
                labelClass = this.props.label.class;
            }

            labelHtml = <div class="bd-highlight pr-10"><Label class={labelClass}> {this.props.label.value} </Label></div>;
        }

        return (
            <div class="d-flex flex-column bd-highlight border info-box">
                <div class="d-flex flex-row bd-highlight p-2 border-bottom item-head">
                    <div class="bd-highlight pr-10 mr-auto"> {this.props.title} </div>
                    {labelHtml}
                </div>

                <div class="bd-highlight">
                    {childrens}
                </div>
                
                <div class="d-flex flex-row bd-highlight mb-2 ml-3 mr-3 border-bottom item-body">
                    
                </div>
            </div>
        );
    }
}

export default InfoBox;