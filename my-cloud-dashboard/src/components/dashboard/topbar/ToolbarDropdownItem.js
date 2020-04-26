import React from 'react';

class ToolbarDropdownItem extends React.Component {

  render() {

    const iconClass = this.props.icon + " " + this.props.color + " white circle";

    var detail = "";

    if (this.props.detail != null) {
      detail = <div class="detail">{this.props.detail}</div>;
    }

    var heading = <i class={iconClass}></i>;

    if (this.props.img != null) {
      heading = <span class="avatar md online">
        <img src={this.props.img.src} alt="..."
          class="rounded-circle" width={this.props.img.size} height={this.props.img.size} />
        <i></i>
      </span>;
    }

    var itemClass = "dropdown-item";
    var display = <a class="dropdown-item" href="#">
      <i class={this.props.icon} aria-hidden="true"></i>
      {this.props.label}</a>;

    if (this.props.type == "media") {
      itemClass = "item-group";
      display = <div class="media">
        <div class="pr-10">
          {heading}
        </div>
        <div class="body">
          <h6 class="heading">{this.props.heading}</h6>
          <time class="meta" datetime="2018-06-12T20:50:48+08:00">{this.props.meta}</time>

          {detail}

        </div>
      </div>
        ;
    }

    return (
      <a class={itemClass} href="#" role="menuitem">
        {display}
      </a>
    );
  }
}

export default ToolbarDropdownItem;