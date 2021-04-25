import React from 'react';

import './Form.css'

class Checkbox extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (<div class="form-group form-check">
            <input type="checkbox" class="form-check-input" id={this.props.id} />
            <label class="form-check-label" for={this.props.id}>{this.props.label}</label>
        </div>);
    }
}

export default Checkbox;