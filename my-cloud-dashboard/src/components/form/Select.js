import React from 'react';

import './Form.css'

class Select extends React.Component {

    constructor(props) {
        super(props);
    }



    render() {

        var values = [];

        if (this.props.values != null) {

            this.props.values.forEach(value => {
                values.push(<option> {value} </option>);
            });
        }

        console.log(this.props.values);
        console.log(values);

        return (<div class="form-floating">
            <select class="form-select" id={this.props.id}>

                {values}

            </select>
            <label for={this.props.id}>{this.props.label}</label>
        </div>);

    }
}

export default Select;