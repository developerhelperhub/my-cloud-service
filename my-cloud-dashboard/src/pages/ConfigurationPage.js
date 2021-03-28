
import React from 'react';
import CodeMirror from 'react-codemirror';

import Label from '../components/dashboard/Label'
import PageContent from '../components/dashboard/PageContent'
import Row from '../components/dashboard/Row'
import Widget from '../components/dashboard/Widget'


import PagePanel from '../components/dashboard/panel/PagePanel'
import PagePanelHead from '../components/dashboard/panel/PagePanelHead'
import PagePanelBody from '../components/dashboard/panel/PagePanelBody'
import PagePanelIcon from '../components/dashboard/panel/PagePanelIcon'

import 'codemirror/lib/codemirror.css';

class ConfigurationPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            code: "test: hello",
            options: {
                lineNumbers: true,
                mode: 'yaml'
            }
        };
        
        this.updateCode = this.updateCode.bind(this);
    }

    updateCode(newCode) {

        this.setState({
			code: newCode,
		});

    }


    render() {
        return (
            <PageContent>
                <Row>
                    <PagePanel cols="col-xxl-7 col-lg-12" >
                        <PagePanelHead title="Configuration">
                            <PagePanelIcon icon="fa fa-refresh"></PagePanelIcon>
                        </PagePanelHead>
                        <PagePanelBody>
                            <CodeMirror value={this.state.code} onChange={this.updateCode}  options={this.state.options}></CodeMirror>
                        </PagePanelBody>
                    </PagePanel>
                </Row>
            </PageContent>
        );

    }
}

export default ConfigurationPage;