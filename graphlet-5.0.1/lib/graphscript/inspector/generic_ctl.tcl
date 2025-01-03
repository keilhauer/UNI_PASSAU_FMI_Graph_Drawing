# This software is distributed under the Lesser General Public License
#
# inspector/main.tcl
#
# control for any attributes
#
#------------------------------------------ CVS
#
# CVS Headers -- The following headers are generated by the CVS
# version control system. Note that especially the attribute
# Author is not necessarily the author of the code.
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/inspector/generic_ctl.tcl,v $
# $Author: forster $
# $Revision: 1.6 $
# $Date: 1999/03/01 16:58:31 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------ CVS
#
# (C) University of Passau 1995-1999, Graphlet Project
#     Author: Michael Forster

package require Graphlet
package provide Graphscript [gt_version]

package require Combobox
package require Numentry
package require Colorbutton

namespace eval ::GT::IS::generic_ctl {

    namespace export create
    
    namespace import ::GT::IS::enable_update
    namespace import ::GT::IS::disable_update
    namespace import ::GT::IS::write_attrs
    
    proc create { IS ctl attrs } {
	variable ::GT::IS::_Value
	variable ::GT::IS::_Constraint
	variable _Widget
	variable _Label

	frame $ctl
	
	foreach attr $attrs {
	    
	    pack [frame $ctl.$attr] -fill x

	    GT::pset { attrSection attrType attrName } [split $attr "-"]

	    set words {}
	    foreach word [split $attrName "_"] {
		lappend words \
		    [string toupper [string index $word 0]][string range $word 1 end]
	    }
	    
	    set label [join $words " "]:
	    
	    pack [label $ctl.$attr.l \
		      -text $label \
		      -width 13 \
		      -anchor w\
		     ] \
		-side left \
		-fill x

	    set widget $ctl.$attr.w
	    set var ::GT::IS::_Value($IS,$attr)

	    disable_update $IS $attr

	    switch [lindex $_Constraint($attr) 0] {
		list {
		    Combobox::create $widget \
			-textvariable $var \
			-values [lrange $_Constraint($attr) 1 end] \
			-width 3
		}
		number {
		    Numentry::create $widget \
			-textvariable $var \
			-width 3 \
			-min [lindex $_Constraint($attr) 1] \
			-max [lindex $_Constraint($attr) 2] \
			-step [lindex $_Constraint($attr) 3]
		}
		color {
		    Colorbutton::create $widget \
			-variable $var
		}
		bool {
		    checkbutton $widget \
			-variable $var \
			-anchor w
		}
		default {
		    entry $widget \
			-textvariable $var \
			-width 3
		}
	    }

	    set _Widget($ctl,$attr) $widget
	    set _Label($ctl,$attr) $ctl.$attr.l
	    
	    enable_update $IS $attr

	    pack $widget \
		-side left \
		-fill x -expand true \
		-padx 1 -pady 1
	}

	return $ctl
    }
    proc update { IS ctl } {
	variable ::GT::IS::_HaveNodes
	variable ::GT::IS::_HaveEdges
	variable ::GT::IS::_Options
	variable ::GT::IS::_Value
	variable _Widget
	variable _Label

	foreach entry [array names _Widget $ctl,*] {
	    set attr [lindex [split $entry ","] end]
	    GT::pset { attrSection attrType attrName } [split $attr "-"]
	    set widget $_Widget($entry)
	    set label $_Label($entry)

	    if { ($attrSection == "n" && $_HaveNodes($IS)) ||
		 ($attrSection == "e" && $_HaveEdges($IS)) ||
		 ($attrSection == "b" &&
		  ($_HaveNodes($IS) || $_HaveEdges($IS)))
	     } {
		$widget configure -state normal
		$label configure -fg $_Options(color,enabled_text)
	    } {
		$widget configure -state disabled
		$label configure -fg $_Options(color,disabled_text)
		set _Value($IS,$attr) {}
	    }
	}
    }
}

#---------------------------------------------------------------------------
#   Set emacs variables
#---------------------------------------------------------------------------
# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; tcl-indent-level: 4 ***
# ;;; End: ***
#---------------------------------------------------------------------------
#   end of file
#---------------------------------------------------------------------------
