import React from 'react';
import {
    BrowserRouter as Router,
    Route,
    Redirect
} from "react-router-dom";

import Dashboard from '../components/dashboard/Dashboard'

import SidebarWrapper from '../components/dashboard/sidebar/SidebarWrapper'
import Sidemenubar from '../components/dashboard/sidebar/Sidemenubar'
import SidemenubarHeader from '../components/dashboard/sidebar/SidemenubarHeader'
import SidebarDropdown from '../components/dashboard/sidebar/SidebarDropdown'
import SidebarSubmenu from '../components/dashboard/sidebar/SidebarSubmenu'
import SidebarMenuitem from '../components/dashboard/sidebar/SidebarMenuitem'
import SidebarHeader from '../components/dashboard/sidebar/SidebarHeader'
import SidebarContent from '../components/dashboard/sidebar/SidebarContent'

import PageContentWrapper from '../components/dashboard/PageContentWrapper'
import Topbar from '../components/dashboard/topbar/Topbar'
import ToolbarIcon from '../components/dashboard/topbar/ToolbarIcon'
import ToolbarItem from '../components/dashboard/topbar/ToolbarItem'
import ToolbarDropdown from '../components/dashboard/topbar/ToolbarDropdown'
import ToolbarDropdownHeader from '../components/dashboard/topbar/ToolbarDropdownHeader'
import ToolbarDropdownFooter from '../components/dashboard/topbar/ToolbarDropdownFooter'
import ToolbarDropdownFooterIcon from '../components/dashboard/topbar/ToolbarDropdownFooterIcon'
import ToolbarDropdownContainer from '../components/dashboard/topbar/ToolbarDropdownContainer'
import ToolbarDropdownItem from '../components/dashboard/topbar/ToolbarDropdownItem'
import ToolbarDropdownProfile from '../components/dashboard/topbar/ToolbarDropdownProfile'
import ToolbarDropdownDivider from '../components/dashboard/topbar/ToolbarDropdownDivider'

import DiscoveryPage from './DiscoveryPage'
import CircuitBreakerPage from './CircuitBreakerPage'

class DashboardPage extends React.Component {


    render() {

        console.log("DashboardPage ---------------")

        return (
            <Dashboard>
                <SidebarWrapper>
                    <SidebarHeader>My Cloud</SidebarHeader>
                    <SidebarContent>
                        <Sidemenubar>
                            {/* <SidemenubarHeader>General</SidemenubarHeader> */}

                            <SidebarDropdown title="Cloud Services" font="fa fa-desktop"
                            // badge={{ label: "New", type: "badge-warning" }}
                            >

                                <SidebarSubmenu>
                                    <SidebarMenuitem label="Discovery" href="/discovery"
                                    // badge={{ label: "Pro", type: "badge-success" }} 
                                    />
                                    <SidebarMenuitem label="Circuit Breaker" href="/circuit-breaker" />
                                </SidebarSubmenu>
                            </SidebarDropdown>


                            {/* <SidebarDropdown title="E-commerce" font="fa fa-shopping-cart"
                                badge={{ label: "3", type: "badge-danger" }}>
                                <SidebarSubmenu>
                                    <SidebarMenuitem label="Products" />
                                    <SidebarMenuitem label="Orders" />
                                    <SidebarMenuitem label="Credit cart" />
                                </SidebarSubmenu>
                            </SidebarDropdown>

                            <SidebarDropdown title="Components" font="far fa-gem">
                                <SidebarSubmenu>
                                    <SidebarMenuitem label="General" />
                                    <SidebarMenuitem label="Panels" />
                                    <SidebarMenuitem label="Tables" />
                                    <SidebarMenuitem label="Icons" />
                                    <SidebarMenuitem label="Forms" />
                                </SidebarSubmenu>
                            </SidebarDropdown>

                            <SidebarDropdown title="Charts" font="fa fa-chart-line">
                                <SidebarSubmenu>
                                    <SidebarMenuitem label="Pie chart" />
                                    <SidebarMenuitem label="Line chart" />
                                    <SidebarMenuitem label="Bar chart" />
                                    <SidebarMenuitem label="Histogram" />
                                </SidebarSubmenu>
                            </SidebarDropdown> */}

                            {/* <SidemenubarHeader>Charts</SidemenubarHeader>

                            <SidebarMenuitem label="Documentation" font="fa fa-book" badge={{ label: "Beta", type: "badge-primary" }} />
                            <SidebarMenuitem label="Calendar" font="fa fa-calendar" />
                            <SidebarMenuitem label="Examples" font="fa fa-folder" /> */}

                        </Sidemenubar>

                    </SidebarContent>

                </SidebarWrapper>

                <PageContentWrapper>
                    <Topbar>
                        <ToolbarIcon align="mr-auto">
                            <ToolbarItem icon="fa fa-search" />
                        </ToolbarIcon>

                        <ToolbarIcon align="navbar-right">

                            <ToolbarDropdown id="dropdownTopMenuNotification" icon="fa fa-bell" badge={{ label: "3", type: "badge-danger" }}>

                                <ToolbarDropdownHeader title="Notifications" badge={{ label: "New 3", type: "badge-danger" }} />

                                <ToolbarDropdownContainer>

                                    <ToolbarDropdownItem type="media" icon="icon wb wb-order" color="bg-red-600"
                                        heading="A new order has been placed"
                                        meta="5 hours ago" />
                                    <ToolbarDropdownItem type="media" icon="icon wb wb-user" color="bg-green-600"
                                        heading="Completed the task"
                                        meta="2 days ago" />
                                    <ToolbarDropdownItem type="media" icon="icon wb wb-settings" color="bg-red-600"
                                        heading="Settings updated"
                                        meta="2 days ago" />
                                    <ToolbarDropdownItem type="media" icon="icon wb wb-calendar" color="bg-blue-600"
                                        heading="Event started"
                                        meta="3 days ago" />
                                    <ToolbarDropdownItem type="media" icon="icon wb wb-chat" color="bg-orange-600"
                                        heading="Message received"
                                        meta="3 days ago" />

                                </ToolbarDropdownContainer>

                                <ToolbarDropdownFooter label="All notifications">
                                    <ToolbarDropdownFooterIcon icon="fa fa-cog" />
                                </ToolbarDropdownFooter>

                            </ToolbarDropdown>


                            <ToolbarDropdown id="dropdownTopMenuMessage" icon="fa fa-envelope" badge={{ label: "5", type: "badge-info" }}>

                                <ToolbarDropdownHeader title="MESSAGES" badge={{ label: "New 5", type: "badge-info" }} />

                                <ToolbarDropdownContainer>

                                    <ToolbarDropdownItem
                                        type="media"
                                        img={{
                                            src: "https://raw.githubusercontent.com/binoykr/top-navbar-bootstrap/master/public/imgs/user-binoy.jpg",
                                            size: "40px"
                                        }}
                                        heading="Mary Adams"
                                        meta="30 minutes ago"
                                        detail="Anyways, i would like just do it" />
                                    <ToolbarDropdownItem
                                        type="media"
                                        img={{
                                            src: "https://raw.githubusercontent.com/binoykr/top-navbar-bootstrap/master/public/imgs/user-binoy.jpg",
                                            size: "40px"
                                        }}
                                        heading="Caleb Richards"
                                        meta="12 hours ago"
                                        detail="I checheck the document. But there seems" />
                                    <ToolbarDropdownItem
                                        type="media"
                                        img={{
                                            src: "https://raw.githubusercontent.com/binoykr/top-navbar-bootstrap/master/public/imgs/user-binoy.jpg",
                                            size: "40px"
                                        }}
                                        heading="June Lane"
                                        meta="2 days ago"
                                        detail="Lorem ipsum Id consectetur et minim" />
                                    <ToolbarDropdownItem
                                        type="media"
                                        img={{
                                            src: "https://raw.githubusercontent.com/binoykr/top-navbar-bootstrap/master/public/imgs/user-binoy.jpg",
                                            size: "40px"
                                        }}
                                        heading="Edward Fletcher"
                                        meta="3 days ago"
                                        detail="Dolor et irure cupidatat commodo nostrud nostrud." />
                                </ToolbarDropdownContainer>

                                <ToolbarDropdownFooter label="See all messages">
                                    <ToolbarDropdownFooterIcon icon="fa fa-cog" />
                                </ToolbarDropdownFooter>

                            </ToolbarDropdown>

                            <ToolbarDropdownProfile id="dropdownTopMenuProfile"
                                title="Hi, Binoy"
                                img={{
                                    src: "https://raw.githubusercontent.com/binoykr/top-navbar-bootstrap/master/public/imgs/user-binoy.jpg",
                                    size: "30px"
                                }}>

                                <ToolbarDropdownItem type="basic" icon="icon wb wb-user" label="Profile" />
                                <ToolbarDropdownItem type="basic" icon="icon wb wb-payment" label="Billing" />
                                <ToolbarDropdownItem type="basic" icon="icon wb wb-settings" label="Settings" />
                                <ToolbarDropdownDivider />
                                <ToolbarDropdownItem type="basic" icon="icon wb wb-power" label="Logout" href="/logout" />

                            </ToolbarDropdownProfile>

                        </ToolbarIcon>

                    </Topbar>


                    <Route exact path="/discovery">
                        <DiscoveryPage />
                    </Route>
                    <Route exact path="/circuit-breaker">
                        <CircuitBreakerPage />
                    </Route>

                </PageContentWrapper>

            </Dashboard>

        );
    }
}

export default DashboardPage;