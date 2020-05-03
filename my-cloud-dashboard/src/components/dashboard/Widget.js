import React from 'react';

import './Widget.css'

class Widget extends React.Component {


  render() {

    var panelClass = "page-panel " + this.props.cols;

    var bodyHeight = "100%";

    if (this.props.height != null) {
      bodyHeight = this.props.height.value + this.props.height.unit;
    }

    return (
      <div class={panelClass}>

        <div class="body" style={{ height: bodyHeight }}>

          <div class="widget widget-default widget-item-icon" onclick="location.href='pages-messages.html';">
            <div class="widget-item-left">
              <span class={this.props.icon}></span>
            </div>
          
            <div class="widget-data">
              <div class="widget-int num-count">{this.props.numcount}</div>
              <div class="widget-title">{this.props.title}</div>
              <div class="widget-subtitle">{this.props.subtitle}</div>
            </div>

            {/* <div class="widget-controls">
              <a href="#" class="widget-control-right widget-remove" data-toggle="tooltip" data-placement="top" title="Remove Widget"><span class="fa fa-times"></span></a>
            </div> */}
          </div>
        </div>

      </div>

    );
  }
}

export default Widget;