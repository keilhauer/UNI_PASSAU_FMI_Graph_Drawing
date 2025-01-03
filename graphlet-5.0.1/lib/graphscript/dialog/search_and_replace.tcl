# This software is distributed under the Lesser General Public License
#
# search_and_replace.tcl
#
# Implements a dialog and functions for search and replace on labels.
#
#------------------------------------------ CVS
#
# CVS Headers -- The following headers are generated by the CVS
# version control system. Note that especially the attribute
# Author is not necessarily the author of the code.
#
# $source: /home/br/CVS/graphlet/src/gt_base/Attributes.h,v $
# $Author: himsolt $
# $Revision: 1.12 $
# $Date: 1999/04/10 15:27:11 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------ CVS
#
# (C) University of Passau 1995-1999, Graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]
package require Combobox

namespace eval GT {
    namespace export \
	action_replace_all \
	action_replace_next \
	action_search_all \
	action_search_and_replace_dialog \
	action_search_dialog_cancel \
	action_search_next \
	action_search_start \
	optionmenu \
	replace \
	search
}


##########################################
#
# GT::action_search_and_replace_dialog editor
#
# Show a dialog for search and replace on labels.
#
##########################################


proc GT::action_search_and_replace_dialog { editor } {

    global GT GT_options
    set graph $GT($editor,graph)

    set dialog $editor.search_and_replace_dialog
    if [winfo exists $dialog] {
	wm deiconify $dialog
	raise $dialog
	focus $dialog.search_label
	return
    }

    set GT($editor,search_and_replace_dialog) $dialog
    toplevel $dialog
    wm title $dialog "Search and Replace"
    wm transient $dialog $editor
    wm resizable $dialog 0 0

    ##########################################
    #
    # Search
    #
    ##########################################

    label $dialog.search_label \
	-text "Find what:" \
	-width 14 \
	-anchor e
    entry $dialog.search_entry \
	-text "" \
	-textvariable GT($dialog,search)


    frame $dialog.direction

    radiobutton $dialog.direction.forward \
	-text "Forward" \
	-value "forward" \
	-variable GT_options(search_direction)
    pack $dialog.direction.forward \
	-side left \
	-anchor w

    radiobutton $dialog.direction.backward \
	-text "Backward" \
	-value "backward" \
	-variable GT_options(search_direction)
    pack $dialog.direction.backward \
	-side left \
	-anchor w

    ##########################################
    #
    # Replace
    #
    ##########################################

    label $dialog.replace_label \
	-text "Replace with:" \
	-width 14 \
	-anchor e
    entry $dialog.replace_entry \
	-text "" \
	-textvariable GT($dialog,replace)


    ##########################################
    #
    # Options
    #
    ##########################################

    #
    # Type
    #

    set type_group [GT::Widgets::group $dialog type "Type" -pack 0]

    radiobutton $type_group.regexp \
	-text "Regexp" \
	-value "regexp"  \
	-variable GT_options(search_type)
    pack $type_group.regexp \
	-side top \
	-anchor w
    GT::tooltips $type_group.regexp "Search for a regular expression"

    radiobutton $type_group.match \
	-text "Match" \
	-value "match" \
	-variable GT_options(search_type)
    pack $type_group.match \
	-side top \
	-anchor w
    GT::tooltips $type_group.match "Search using Tcl match expressions"

    radiobutton $type_group.string \
	-text "Exact" \
	-value "string" \
	-variable GT_options(search_type)
    pack $type_group.string \
	-side top \
	-anchor w
    GT::tooltips $type_group.string "Search for a string"


    #
    # Direction
    #

#     set GT($dialog,search_direction) forward

#     set direction_group \
# 	[GT::Widgets::group $dialog direction "Direction" -pack 0]

#     radiobutton $direction_group.forward \
# 	-text "Forward" \
# 	-value "forward" \
# 	-variable GT($dialog,search_direction) \
# 	-underline 0
#     pack $direction_group.forward \
# 	-side top \
# 	-anchor w

#     radiobutton $direction_group.backward \
# 	-text "Backward" \
# 	-value "backward" \
# 	-variable GT($dialog,search_direction) \
# 	-underline 0
#     pack $direction_group.backward \
# 	-side top \
# 	-anchor w
    

    #
    # Domain
    #


    set domain_group [GT::Widgets::group $dialog domain "Domain" -pack 0]

    radiobutton $domain_group.all \
	-text "All" \
	-variable GT_options(search_domain) \
	-value all
    radiobutton $domain_group.nodes \
	-text "Nodes" \
	-variable GT_options(search_domain) \
	-value nodes
    radiobutton $domain_group.edges \
	-text "Edges" \
	-variable GT_options(search_domain) \
	-value edges

    pack \
	$domain_group.all \
	$domain_group.nodes \
	$domain_group.edges \
	-anchor w

    ##########################################
    #
    # Buttons
    #
    ##########################################

    #     button $dialog.search_start \
	# 	-text "First" \
	# 	-command "GT::button_action $editor search_start"
    #     GT::tooltips $dialog.search_start \
	# 	"Start searching for an object which matches the search string and select it."

    button $dialog.search_next \
	-text "Find Next" \
	-command "GT::button_action $editor search_next"
    GT::tooltips $dialog.search_next \
	"Search the next object and select it."

    button $dialog.search_all \
	-text "Find All" \
	-command "GT::button_action $editor search_all"
    GT::tooltips $dialog.search_all \
	"Search all objects which match the search string and select them."


    button $dialog.replace_next \
	-text "Replace Next" \
	-command "GT::button_action $editor replace_next"
    GT::tooltips $dialog.replace_next \
	"Replace the selected label, search the next object next FORWARD and select it."

    button $dialog.replace_all \
	-text "Replace All" \
	-command "GT::button_action $editor replace_all"
    GT::tooltips $dialog.replace_all \
	"Search and replace all labels, and select the objects."


    #
    # 3rd row: help, cancel
    #

    button $dialog.cancel \
	-text Cancel \
	-command "GT::button_action $editor search_dialog_cancel"
    button $dialog.help \
	-text Help \
	-state disabled

    grid $dialog.search_label $dialog.search_entry - $dialog.search_next \
	-sticky ew \
	-padx 5 \
	-pady 2
    grid x $dialog.direction - $dialog.search_all \
	-sticky ew \
	-padx 5 \
	-pady 2

    grid $dialog.replace_label $dialog.replace_entry - $dialog.replace_next \
	-sticky ew \
	-padx 5 \
	-pady 2
    grid x $dialog.type $dialog.domain $dialog.replace_all \
	-sticky ewn \
	-padx 5 \
	-pady 2

    grid x x x $dialog.cancel \
	-sticky ews \
	-padx 5 \
	-pady 2 \
	-row 3

    raise $dialog
    focus $dialog.search_label
}



##########################################
#
# GT::search editor args
#
# Arguments:
#
# -search string
# -replace string (wont replace if omitted or {})
#
# -type exact|match|regexp
# -domain all|node|edge|<<selection>>
# -direction forward|backward|all
# -start_object <<node or edge>>
# -mode all|single
#
##########################################


proc GT::search { editor args } {

    global GT GT_options
    set graph $GT($editor,graph)

    #
    # Parse arguments
    #

    set search {}
    set replace {}

    set selection  0
    set type match
    set domain all
    set direction 1
    set mode all

    for { set i 0 } { $i < [llength $args] } { incr i } {
	switch -regexp -- [lindex $args $i] {
	    -search {
		incr i
		set search [lindex $args $i]
	    }
	    -replace {
		incr i
		set search [lindex $args $i]
	    }
	    -type {
		incr i
		set type [lindex $args $i]
	    }
	    -domain {
		incr i
		set domain [lindex $args $i]
	    }
	    -direction {
		incr i
		switch -glob [lindex $args $i] {
		    f* {
			set direction 1
		    }
		    b* {
			set direction -1
		    }
		    default {
			error "Illegal value [lindex $args $i] for -direction"
		    }
		}
		set mode single
	    }
	    -start_object {
		incr i
		set start_object [lindex $args $i]
	    }
	    -mode {
		set mode [lindex $args [incr i]]
	    }
	}
    }

    #
    # Find out which objects to search
    #

    switch -glob $domain {
	all {
	    set objects [concat [$graph nodes] [$graph edges]]
	}
	node* {
	    set objects [$graph nodes]
	}
	edge* {
	    set objects [concat [$graph nodes] [$graph edges]]
	}
    }

    #
    # Look for start object
    #

    if {[info exists start_object] && $start_object != {}} {
	set start [lsearch $objects $start_object]
	if { $start == -1 } {
	    set start -1
	} else {
	    incr start $direction
	}
    } else {
	set start 0
    }

    #
    # Search
    #

    for {set i $start} {0<=$i && $i < [llength $objects]} {incr i $direction} {

	set object [lindex $objects $i]
	set label [$graph get $object -label]
	set found 0
	switch -regexp -- $type {
	    string|String|exact|Exact {
		set found [expr [string compare $search $label] == 0]
	    }
	    match|Match {
		set found [expr [string match $search $label] == 1]
	    }
	    regexp|Regexp {
		set found [expr [regexp -- $search $label] == 1]
	    }
	}

	if {$found != 0} {
	    lappend found_objects $object
	    if {$mode == "single"} {
		break
	    }
	}
    }

    if [info exists found_objects] {
	return $found_objects
    } else {
	return {}
    }

}


proc  GT::replace { editor objects args } {

    global GT GT_options
    set graph $GT($editor,graph)

    #
    # Parse arguments
    #

    for { set i 0 } { $i < [llength $args] } { incr i } {
	switch -- [lindex $args $i] {
	    -search {
		incr i
		set search [lindex $args $i]
	    }
	    -replace {
		incr i
		set replace [lindex $args $i]
	    }
	    -type {
		incr i
		set type [lindex $args $i]
	    }
	}
    }

    if ![info exists type] {
	set type all
    }

    foreach object $objects {

	switch -regexp -- $type {
	    string|String|exact|Exact|match|Match {
		$graph configure $object -label $replace
		lappend replaced_objects $object
	    }
	    regexp|Regexp {
		set label [$graph get $object -label]
		if {[regsub -- $search $label $replace result] > 0 } {
		    $graph configure $object -label $result
		    lappend replaced_objects $object
		}
	    }
	}
    }
}


##########################################
#
# Search dialog actions
#
# GT::action_search_start
# GT::action_search_next
# GT::action_search_all
#
# GT::action_replace_start
# GT::action_replace_next
# GT::action_replace_all
#
# GT::action_search_dialog_cancel
#
##########################################


# proc GT::action_search_start {editor} {

#     global GT
#     if [info exists GT($editor,search_and_replace_dialog)] {
# 	set dialog $GT($editor,search_and_replace_dialog)
#     } else {
# 	return {}
#     }

#     set GT($dialog,last) [GT::search $editor \
    # 			      -search $GT($dialog,search) \
    # 			      -start {} \
    # 			      -step]

#     GT::select $editor $GT($dialog,last)
#     if {$GT($dialog,last) != {}} {
# 	GT::guarantee_visible $editor selection
#     } else {
# 	GT::message $editor "No objects found" warning
#     }

#     return $GT($dialog,last)
# }


proc GT::action_search_next {editor {replace 0}} {

    global GT
    set graph $GT($editor,graph)

    if [info exists GT($editor,search_and_replace_dialog)] {
	set dialog $GT($editor,search_and_replace_dialog)
    } else {
	return {}
    }
    
    if ![info exists GT($dialog,last)] {
	set GT($dialog,last) {}
    }

    set GT($dialog,last) [GT::search $editor \
			      -search $GT($dialog,search) \
			      -type $GT_options(search_type) \
			      -domain $GT_options(search_domain) \
			      -direction $GT_options(search_direction) \
			      -start_object $GT($dialog,last)]

    if {$replace} {
	GT::replace $editor $GT($dialog,last) \
	    -search $GT($dialog,search) \
	    -replace $GT($dialog,replace) \
	    -type $GT_options(search_type)
	$graph draw
    }

    GT::select $editor $GT($dialog,last)
    if {$GT($dialog,last) != {}} {
	GT::guarantee_visible $editor selection
    } else {
	GT::message $editor "No objects found" warning
    }

    return $GT($dialog,last)
}


proc GT::action_search_all {editor {replace 0}} {

    global GT
    set graph $GT($editor,graph)

    if [info exists GT($editor,search_and_replace_dialog)] {
	set dialog $GT($editor,search_and_replace_dialog)
    } else {
	return {}
    }
    
    catch {unset GT($dialog,last)}
    set found [GT::search $editor \
		   -search $GT($dialog,search) \
		   -type $GT_options(search_type) \
		   -domain $GT_options(search_domain) \
		   -mode all]

    if {$replace} {
	GT::replace $editor $found \
	    -search $GT($dialog,search) \
	    -replace $GT($dialog,replace) \
	    -type $GT_options(search_type)
	$graph draw
    }

    GT::select $editor $found
    if {$found != {}} {
	GT::guarantee_visible $editor selection
	GT::message $editor "[llength $found] labels replaced"
    } else {
	GT::message $editor "No objects found" warning
    }

    return $found
}


proc GT::action_replace_next {editor} {

    return [GT::action_search_next $editor 1]
}


proc GT::action_replace_all {editor} {

    return [GT::action_search_all $editor 1]

}


proc GT::action_search_dialog_cancel {editor} {

    global GT
    if [info exists GT($editor,search_and_replace_dialog)] {
	set dialog $GT($editor,search_and_replace_dialog)
    } else {
	return {}
    }
    
    #     foreach i {search replace last} {
    # 	if [info exists GT($dialog,$i)] {
    # 	    unset GT($dialog,$i)
    # 	}
    #     }

    #     destroy $dialog
    wm withdraw $dialog
}


#
# Utility to create an option menu
#

proc GT::optionmenu { frame label width options variable } {

    frame $frame
    label $frame.label \
	-text $label \
	-width $width \
	-anchor e
    pack $frame.label \
	-side left
    menubutton $frame.choices \
	-menu $frame.choices.menu \
	-textvariable GT_options($variable) \
	-indicatoron true
    menu $frame.choices.menu \
	-tearoff 0
    foreach i $options {
	$frame.choices.menu add radiobutton \
	    -label $i \
	    -variable GT_options($variable)
    }
    pack $frame.choices
    return $frame
}


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
