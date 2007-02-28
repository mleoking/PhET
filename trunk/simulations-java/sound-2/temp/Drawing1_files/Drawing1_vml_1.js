
function VMLLoadFunction()
{
	m_viewMgr.onLoad();
	m_viewMgr.onResize();
}

function ViewMgrSetZoom(val)
	{
	if ( !this.s )
		return;

	if ( val == this.zoomFactor )
		return;

	if ( (2 <= val) && (3000 >= val) )
		{
		this.zoomLast = this.zoomFactor;
		this.zoomFactor = (val / 100);
		document.body.scroll= "yes";
		this.ApplyZoom();
		}
	else if ( -1 == val )
		{
		this.zoomLast = this.zoomFactor;
		this.zoomFactor = val;
		document.body.scroll= "no";
		this.onResize();
		}
	}

function ViewMgrGetZoom()
	{
	return this.zoomFactor;
	}


function ViewMgrApplyZoom()
	{
	var f, cx, cy, pw, ph, vw, vh;

	f = this.zoomFactor / (this.s.pixelWidth / this.origWidth);

	vw = document.body.clientWidth;
	vh = document.body.clientHeight;

	cx = f * (document.body.scrollLeft + (vw / 2) - this.s.posLeft);
	cy = f * (document.body.scrollTop + (vh / 2) - this.s.posTop);

	pw = f * this.s.pixelWidth;
	ph = f * this.s.pixelHeight;

	this.s.pixelWidth = pw;
	this.s.pixelHeight = ph;

	if ( pw <= vw )
		this.s.posLeft = (vw / 2) - cx;
	else
		{
		var left = cx - (vw / 2);

		if ( left >= 0 )
			{
			this.s.posLeft = 0;
			window.scrollBy(-document.body.scrollLeft, 0);
			window.scrollBy(left - document.body.scrollLeft, 0);
			}
		else
			{
			this.s.posLeft = -left;
			window.scrollBy(-document.body.scrollLeft, 0);
			}
		}

	if ( ph <= vh )
		this.s.posTop = (vh / 2) - cy;
	else
		{
		var top = cy - (vh / 2);

		if ( top >= 0 )
			{
			this.s.posTop = 0;
			window.scrollBy(0, -document.body.scrollTop);
			window.scrollBy(0, top - document.body.scrollTop);
			}
		else
			{
			this.s.posTop = -top;
			window.scrollBy(0, -document.body.scrollTop);
			}
		}
	}


function ViewMgrOnResize()
	{
	if ( -1 != this.zoomFactor )
		return;

	var w, h;

	cltWidth = document.body.clientWidth - (2 * cxmgn);
	cltHeight = document.body.clientHeight - (2 * cymgn);

	cltWH = cltWidth / cltHeight;

	if ( cltWH < this.origWH )
		{
		w = cltWidth;
		h = w / this.origWH;
		}
	else
		{
		h = cltHeight;
		w = h * this.origWH;
		}

	this.s.pixelWidth = w;
	this.s.pixelHeight = h;
	this.s.posLeft = cxmgn + (cltWidth - w) / 2;
	this.s.posTop = cymgn + (cltHeight - h) / 2;
	}
