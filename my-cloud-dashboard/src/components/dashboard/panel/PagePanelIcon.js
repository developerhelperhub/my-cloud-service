import React from 'react';

class PagePanelIcon extends React.Component {

    render() {
        var panelIconClass = "panel-icon-font";

        if (this.props.icon != null) {
            panelIconClass = panelIconClass + " " + this.props.icon;
        }
        
        return (
            <div class="panel-icon"><span class={panelIconClass}  onClick={event => this.props.event(event)} ></span></div>
        );
    }
}

export default PagePanelIcon;