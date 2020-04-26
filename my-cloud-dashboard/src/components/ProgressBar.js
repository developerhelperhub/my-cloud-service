import React from 'react';

class ProgressBar extends React.Component {


  render() {

    var progressClass = "progress-bar " + this.props.style;

    return (

      <div class="progress">
        <div class={progressClass} role="progressbar"
          aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style={{ width: this.props.percentage }}>{this.props.children}</div>
      </div>

    );
  }
}

export default ProgressBar;