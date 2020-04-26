import React from 'react';

class ToolbarDropdownProfile extends React.Component {


  render() {
    const childrens = React.Children.map(this.props.children, children =>
      React.cloneElement(children)
    );

    return (
      <li class="nav-item dropdown">
        <a class="nav-link" href="#" id={this.props.id} data-toggle="dropdown" aria-expanded="false">

          <span>{this.props.title}</span>
          <span class="online">
            <img src={this.props.img.src}
              class="rounded-circle" width={this.props.img.size} height={this.props.img.size} />
          </span>
        </a>

        <div class="dropdown-menu dropdown-menu-right" aria-labelledby={this.props.id}>
          <div class="dropdown-menu-container">

            {childrens}

          </div>
        </div>
      </li>

    );
  }
}

export default ToolbarDropdownProfile;