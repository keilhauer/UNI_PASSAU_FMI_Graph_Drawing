#ifdef UNIX5
#ifndef lint
static char version_id[] = "%Z% File : %M% %E%  %U%  Version %I% Copyright (C) 1992/93 Schweikardt Andreas";
#endif
#endif 

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	%M%

	Date	:	%E%	(%U%)

	Version	:	%I%

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Interface (graphic):	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of %M% :




********************************************************************************


Functions of %M% :



*******************************************************************************/


/******************************************************************************
*                                                                             *
*			standard includes				      *
*                                                                             *
*******************************************************************************/

#include <math.h>

/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/algorithm.h>
#include <search/control.h>
#include <search/move.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*			local defines 		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local macros  		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local functions					      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			global variables				      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/



/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/

/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/


void	NodesAndEdgesSearch(Sgraph graph, Method method)
{
	unsigned int	n,
			e = 0;
	double		result,
			help;
	unsigned int	searchers;

	Snode	node,
		neighbour;
	Slist	list,
		elt;

	if ( method != METHOD_NODE_SEARCH )
	{
	    for_all_nodes( graph, node )
	    {
		    list = make_slist_of_sourcelist( node );
		    for_slist( list, elt )
		    {
			neighbour = (Snode)attr_data( elt );
			if ( neighbour != node )
				e++;
		    } end_for_slist( list, elt )
		    free_slist( list );

	    } end_for_all_nodes( graph, node );

	    e = e/2;
     


	}
	else
	{
		e = COedges();
	}	
	

	n = COnodes();

	help = (double)(n-0.5);

	
	result = help - sqrt( help*help-2*(double)e);	

	
	searchers = (unsigned int)ceil( result ) +1;	

	if ( method != METHOD_NODE_SEARCH )
	{
		searchers--;
	}

	COsetHiddenMaxSearchers( searchers );

	return;
}


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
