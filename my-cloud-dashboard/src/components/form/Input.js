import React from 'react';

import './Form.css'

class Input extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (<div class="form-floating">
            <input type={this.props.type} id={this.props.id} class="form-control" placeholder={this.props.label} />
            <label for={this.props.id}>{this.props.label}</label>
        </div>);
    }
}

export default Input;