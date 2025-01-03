# This software is distributed under the Lesser General Public License
#
# create_mode.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/modes/create_mode.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:42:20 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
#


package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	action_create_mode \
	adjust_coordinates_for_grid \
	ev_create_node \
	ev_create_edge_start \
	ev_create_edge_motion \
	ev_create_edge_end \
	ev_create_mode_stop \
	ev_create_mode_undo_last_bend \
	leave_create_mode \
	finish_create_edge \
	new_edge_test
}



###########################################
#
# Create mode action
#
###########################################


proc GT::action_create_mode  { editor  } {
    GT::switch_to_mode $editor create_mode
}



##########################################
#
# UTILITIES
#
# GT::adjust_coordinates_for_grid
#
##########################################


proc GT::adjust_coordinates_for_grid { editor x_var y_var {source event}} {

    global GT GT_event GT_options
    upvar $x_var x
    upvar $y_var y

    set graph $GT($editor,graph)
    set canvas $GT($editor,canvas)

    switch $source {
	event {
	    set x [$graph translate $canvas -reverse 1 -x $x]
	    set y [$graph translate $canvas -reverse 1 -y $y]
	}
	graph {
	    set x [$graph translate $canvas -x $x]
	    set y [$graph translate $canvas -y $y]
	}
    }

    set gw [GT::get_option $editor grid 0]
    if { $gw != {} && $gw != 0 } {
	set x [expr round($x / $gw) * $gw]
	set y [expr round($y / $gw) * $gw]
    }

    set minx [lindex [$canvas cget -scrollregion] 0]
    set miny [lindex [$canvas cget -scrollregion] 1]
    set maxx [lindex [$canvas cget -scrollregion] 2]
    set maxy [lindex [$canvas cget -scrollregion] 3]

    if {$x < $minx} {
	set x $minx
    }
    if {$y < $miny} {
	set y $miny
    }
    if {$x > $maxx} {
	set x $maxx
    }
    if {$y > $maxy} {
	set y $maxy
    }

}


##########################################
#
# Event handlers for node creation operations
#
##########################################


proc GT::ev_create_node {args} {

    global GT GT_event GT_options
    set editor $GT_event(editor)

    if { [info exists GT_event(create_edge_source_node)] == 0 } {

	set x $GT_event(canvasx)
	set y $GT_event(canvasy)
	GT::adjust_coordinates_for_grid $editor x y

	set canvas $GT_event(W)
	set under_cursor [$canvas find withtag current]

	if { $under_cursor == "" } {

	    set graph $GT($editor,graph)
	    set node [$graph new_node]
	    $graph configure $node -x $x -y $y
	    $graph draw

	    if $GT_options(select_after_create) {
		GT::select $editor select $node
	    }

	    GT::undo $editor newframe
	    GT::undo $editor delete $node

	}
	
    }
    
    return -code break
}


##########################################
#
# Event handlers for edge creation operations
#
##########################################


proc GT::ev_create_edge_start {args} {

    global GT GT_event

    set editor $GT_event(editor)
    set canvas $GT($editor,canvas)
    set graph $GT($editor,graph)
    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    GT::adjust_coordinates_for_grid $editor x y

    set GT_event(last_x) $x
    set GT_event(last_y) $y

    set node [GT::find_object $editor node current]
    if { $node != {} } {
	if {[info exists GT_event(create_edge_source_node)] == 0} {
	    GT::select $GT_event(editor) remove selection
	    set GT_event(create_edge_source_node) $node

	    set canvas_x [$graph translate $canvas -x $x]
	    set canvas_y [$graph translate $canvas -y $y]
	    $GT_event(W) create line \
		$canvas_x $canvas_y \
		$canvas_x $canvas_y \
		-tag GT::temporary_edge
	    GT::mark_possible_endnode $editor $canvas_x $canvas_y

	} else {

	    # finishing edge 
	}

    } else {

	GT::finish_create_edge $GT_event(W)
	bell
    }

    return -code break
}



proc GT::ev_create_edge_motion {args} {

    global GT_event

    if [info exists GT_event(create_edge_source_node)] {

	#
	# Move the end of the temporary edgeline.
	#

	set x $GT_event(canvasx)
	set y $GT_event(canvasy)
	GT::adjust_coordinates_for_grid $GT_event(editor) x y
	set canvas $GT_event(W)
	set graph $GT_event(graph)
    
	set move_x [expr $x - $GT_event(last_x)]
	set move_y [expr $y - $GT_event(last_y)]
    
	#
	# Add two more coordinates to GT::temporary_edge
	#

	set canvas_x [$graph translate $canvas -x $x]
	set canvas_y [$graph translate $canvas -y $y]
	set coords [$canvas coords GT::temporary_edge]
	set coords [lrange $coords 0 [expr [llength $coords] - 3]]
	lappend coords $canvas_x $canvas_y
	eval $canvas coords GT::temporary_edge $coords

	GT::mark_possible_endnode $GT_event(editor) $canvas_x $canvas_y

	set GT_event(last_x) $x
	set GT_event(last_y) $y
    }

    return -code break
}


proc GT::ev_create_edge_end {args} {
    
    global GT_event GT_options

    set editor $GT_event(editor) 
    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    GT::adjust_coordinates_for_grid $editor x y
    set canvas $GT_event(W)
    set graph $GT_event(graph)

    if { [info exists GT_event(create_edge_source_node)] == 0 } {
	GT::debug { puts "No start object" }
	return -code break
    }

    #
    # Find a node close to the current position
    #

    $canvas addtag GT::closest overlapping \
	[expr [$graph translate $canvas -x $x] - $GT_options(overlap_gap)] \
	[expr [$graph translate $canvas -y $y] - $GT_options(overlap_gap)] \
	[expr [$graph translate $canvas -x $x] + $GT_options(overlap_gap)] \
	[expr [$graph translate $canvas -y $y] + $GT_options(overlap_gap)]
    set node [GT::find_object $editor node GT::closest]
    $canvas dtag GT::closest

    #
    # Take a closer look at the end node
    #

    if { $node == "" } {

	#
	# Its a bend !
	#

	if $GT_options(allow_bends) {
	    set canvas_x [$graph translate $canvas -x $x]
	    set canvas_y [$graph translate $canvas -y $y]
	    set coords [$canvas coords GT::temporary_edge]
	    set coords [lrange $coords 0 [expr [llength $coords] - 3]]
	    lappend coords $canvas_x $canvas_y $canvas_x $canvas_y
	    eval $canvas coords GT::temporary_edge $coords
	} else {
	    GT::message $editor "Bends are disabled" error
	    GT::finish_create_edge $canvas
	}

    } else {
	
	#
	# It's the end of the edge !
	#

	set source $GT_event(create_edge_source_node)
	set target $node
	set line [$graph translate $canvas -reverse 1 \
		      [$canvas coords GT::temporary_edge]]

	if { $source != $target || [llength $line] > 4 } {

	    set test [new_edge_test $graph $source $target]

	    if { $test == {} } {

		set edge [$graph new_edge $source $target]
		$graph configure $edge -line $line
		$graph draw
		
		if $GT_options(select_after_create) {
		    GT::select $editor select $edge
		}

		GT::undo $editor newframe
		GT::undo $editor delete $edge

	    } else {
		eval GT::message $editor $test
	    }
	    
	} else {

	    GT::select $GT_event(editor) select $source

	}

	GT::finish_create_edge $canvas

    }

    return -code break
}


##########################################
#
# GT::create_mode_stop
# GT::create_mode_undo_last_bend
#
##########################################


proc GT::ev_create_mode_stop { args } {

    global GT_event

    if [info exists GT_event(create_edge_source_node)] {
	GT::finish_create_edge $GT_event(W)
    }

    return -code break
}



proc GT::ev_create_mode_undo_last_bend { args } {

    global GT_event
    set canvas $GT_event(W)

    if [info exists GT_event(create_edge_source_node)] {
	set coords [$canvas coords GT::temporary_edge]

	#
	# Shorten the coordinates in GT::temporary_edge. This must
	# be done as follows:
	# 1. Remove the last *four* coordinate pairs.
	# 2. Add a copy of the now last coordinate pair.
	# This must be done to ensure that the last two pairs are
	# equal as long as the mouse is down -- MH, 1/1/97
	#

	set coords [$canvas coords GT::temporary_edge]
	set l [llength $coords]

	if { $l > 6 } {
	    set coords [concat [lrange $coords 0 [expr $l-5]] \
			    [lindex $coords [expr $l-6]] \
			    [lindex $coords [expr $l-5]]]
	    eval $canvas coords GT::temporary_edge $coords
	} else {
	    GT::finish_create_edge $GT_event(W)
	}
    }

    return -code break
}



##########################################
#
# GT::leave_create_mode
#
##########################################

proc GT::leave_create_mode { editor mode } {

    global GT_event

    if [info exists GT_event(create_edge_source_node)] {
	return "Unfinished Edge."
    } else {
	return {}
    }
}


##########################################
#
# Utility procedures
#
##########################################


proc GT::finish_create_edge canvas {
    global GT_event
    $canvas delete GT::temporary_edge
    $canvas delete GT::temporary_endnode_indicator
    unset GT_event(create_edge_source_node)
}



proc GT::mark_possible_endnode { editor x y } {

    global GT GT_options GT_event
    set canvas $GT($editor,canvas)

    #
    # If we are near a node, indicate the node.
    #
    
    $canvas delete GT::temporary_endnode_indicator
	$canvas addtag GT::closest overlapping \
	[expr $x - $GT_options(overlap_gap)] \
	[expr $y - $GT_options(overlap_gap)] \
	[expr $x + $GT_options(overlap_gap)] \
	[expr $y + $GT_options(overlap_gap)]
    set node [GT::find_object $editor node GT::closest]
    $canvas dtag GT::closest

    if { $node != {} } {

	set graph $GT_event(graph)
	set x [$graph translate $canvas -x [$graph get $node -x]]
	set y [$graph translate $canvas -y [$graph get $node -y]]
	set w [$graph translate $canvas -x [$graph get $node -w]]
	set h [$graph translate $canvas -y [$graph get $node -h]]

	if { $GT_options(temporary_endnode_indicator_color) == {} } {
	    set outline [$graph get $node -outline]
	} else {
	    set outline $GT_options(temporary_endnode_indicator_color)
	}

	$canvas create rectangle \
	    [expr $x - $w/2 - $GT_options(overlap_gap)] \
	    [expr $y - $h/2 - $GT_options(overlap_gap)] \
	    [expr $x + $w/2 + $GT_options(overlap_gap)] \
	    [expr $y + $h/2 + $GT_options(overlap_gap)] \
	    -outline $outline \
	    -tag GT::temporary_endnode_indicator
    }
}



##########################################
#
# Test wether a given edge is allowed
#
##########################################

proc GT::new_edge_test { graph source target } {

    global GT_options

    if { $source == $target } {

	if { $GT_options(allow_self_loops) == 0 } {
	    return { "Self loops are disabled" error }
	}
	    
    } elseif {[$graph edges -between $source $target] != {}
	      && !$GT_options(allow_multiple_edges) } {
	
	return { "Multiple edges are disabled" error }
    }
    
    return {}
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
